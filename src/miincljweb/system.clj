(ns miincljweb.system
  "This is screaming to switch to Components"
  (:require
   [clojure.tools.nrepl.server :as nrepl-server]
   [compojure.handler :as handler]
   [clojure.core.reducers :as r]
   [miincljweb.config :as cfg]
   [miincljweb.routes :as routes]
   [org.httpkit.server :as server]
   [taoensso.timbre :as timbre
    :refer (trace debug info warn error fatal spy with-log-level)])
  (:gen-class))


;;;; Based on Stuart Sierra's workflow
;;;; (thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded)
;;;; Probably stupid and pointless to include that here, but at
;;;; least it's some sort of skeletal
;;;; reminder about how to do things.

(defn init
  "This approach is mixing up concerns:
I really have a collection of sites, each with its own handlers/stop fn"
  [site-descriptions]
  ;; FIXME: Switch to using slingshot
  {:running nil
   :sites site-descriptions
   :repl nil})

(defn start-web-server
  [description]
  (let [port (:port description)
        router (:router description)
        sd (server/run-server (handler/site router)
                              {:port port})]
    (trace "Started " (:domain description) " on port " port)
    (into description
          {
           ;; Q: Is handler worth keeping a reference to this around?
           ;; A: Absolutely.
           ;; Compojure isn't super-friendly for this sort of thing,
           ;; but it *can* be called.
           :handler (handler/site router)
           :running true
           :shut-down sd})))

(defn start
  "Performs side-effects to initialize system, acquire resources, and start it running.
Returns an updated instance of the system.
Dangerous: if this throws an exception, it could easily lock a resource with no way to
release. Pretty much the only way out then is to restart the JVM."
  [server]
  ;; This lets the end-user customize the sites without
  ;; updating the config.
  ;; Mostly useful when using this as a library.
  (if-let [sites (:sites server)]
    ;; Really seems like I could be doing this with a
    ;; something like a reducer for truly gigantic
    ;; sites.
    ;; Or maybe using take to set up a few (based on
    ;; core count) of seqs to map into a pmap. Going
    ;; through the list twice is quite wasteful.
    ;; Then again, this shouldn't be run very often.
    ;; And, if you're trying to maintain that many sites,
    ;; you should probably be using something like
    ;; jboss.
    (let [started-sites (doseq [site sites]
                          (start-web-server site))
          repl (nrepl-server/start-server :port (cfg/repl-port))]
      {:running (promise)
       :sites started-sites
       :repl repl})
    (error "Missing sites in" server)))

(defn stop
  "Performs side-effects to stop system and release its resources.
Returns an updated instance of the system.
Dangerous in pretty much exactly the same way as start."
  [server]
  (trace server)
  (when-let [running (:running server)]
    (doseq [site (:sites server)]
      (when (:running site)
        (trace "Stopping: " (:domain site))
        ((:shut-down site))))
    (when-let [repl-stopper (:repl server)]
      (repl-stopper))

    ;; Q: Does this deliver the "real" thing?
    (deliver running true))

  ;; Then just start over with a blank slate
  (init (map (fn [site]
               (assoc site :running false))
             (:sites server))))

