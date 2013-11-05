(ns miincljweb.system
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
  []
  ;; FIXME: Switch to using slingshot
  {:shut-down (atom (fn [] (throw (RuntimeException. "Not running"))))
   :sites (atom nil)
   :repl (atom nil)
   ;; For lein-ring.
   :handler (atom nil)})

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
  (if-let [sites-atom (:sites server)]
    (when-not @sites-atom
      (reset! (:sites server) (cfg/sites)))
    (error "Missing sites in" server))

  (let [sites @(:sites server)]
    ;; Really seems like I could be doing this with a
    ;; something like a reducer for truly gigantic
    ;; sites.
    ;; Or maybe using take to set up a few (based on
    ;; core count) of seqs to map into a pmap. Going
    ;; through the list twice is quite wasteful.
    ;; Then again, this shouldn't be run very often.
    (let [started-sites (map start-web-server @(:sites server))]
      ;; Make sure they're realized
      ;;(doseq started-sites)
      (reset! (:sites server) started-sites))

    ;; This will allow one site to be shut down at a time.
    ;; Things will get broken quickly if you don't coordinate
    ;; the shut-down function
    ;; TODO: Add a function for shutting down an individual site.
    ;; It will have to update the :sites atom.
    ;; Actually, if that sort of thing is going to be common,
    ;; I need to rethink the fundamental data structure
    (reset! (:shut-down server) (fn []
                                  (doseq [site @(:sites server)]
                                    (when (:running site)
                                      (trace "Stopping: " (:domain site))
                                      ((:shut-down site)))))))

  (let [repl-port (cfg/repl-port)]
    (reset! (:repl server) (nrepl-server/start-server :port repl-port)))

  server)

(defn stop
  "Performs side-effects to stop system and release its resources.
Returns an updated instance of the system.
Dangerous in pretty much exactly the same way as start."
  [server]
  (trace server)
  (try
    (if-let [shut-down-atom (:shut-down server)]
      (if-let [shut-down-method @shut-down-atom]
        (shut-down-method)
        (warn "No web shutdown method"))
      (warn "No web shutdown atom"))
    (catch Throwable ex
      (error "Shutting down web servers failed:" ex)))
  (try
    (if-let [stop-repl-atom (:repl server)]
      (if-let [repl-stopper @stop-repl-atom]
        (nrepl-server/stop-server repl-stopper)
        (warn "No nREPL server to stop"))
      (warn "Missing nREPL server atom"))
    (catch Throwable ex
      (error "Shutting down nREPL failed:" ex)))

  (if-let [sites (:sites server)]
    (reset! sites nil)
    (warn "Missing sites atom"))
  (if-let [shutdown (:shut-down server)]
    (reset! shutdown (fn [] (throw (RuntimeException. "Not running"))))
    (warn "Missing web server(s) shutdown atom"))
  (if-let [repl (:repl server)]
    (reset! repl nil)
    (warn "Missing REPL atom"))

  (trace server)

  server)

