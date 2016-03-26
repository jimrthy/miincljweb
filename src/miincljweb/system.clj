(ns miincljweb.system
  "Skeleton that provides the structure for everything else to build around"
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.tools.nrepl.server :as nrepl-server]
   [clojure.core.reducers :as r]
   [com.stuartsierra.component :as cpt]
   [miincljweb.config :as cfg]
   [miincljweb.routes :as routes]
   [miincljweb.server :as web]
   [miincljweb.sites :as sites]
   [schema.core :as s]
   [taoensso.timbre :as timbre
    :refer (trace debug info warn error fatal spy with-log-level)])
  (:import
   [com.stuartsierra.component SystemMap]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Schema

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Internal

(comment
  (defn obsolete-start
    "Performs side-effects to initialize system, acquire resources, and start it running.
  Returns an updated instance of the system.
  Dangerous: if this throws an exception, it could easily lock a resource with no way to
  release. Pretty much the only way out then is to restart the JVM."
    [server]
    (throw (ex-info "Obsolete" {:replacement "individual components"}))
    ;; Actually, individual components need to handle their own startup/shutdown
    ;; This lets the end-user customize the sites without
    ;; updating the config.
    ;; Mostly useful when using this as a library.
    ;; And there really isn't any excuse for trying to use this in
    ;; any other way
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
            ;; Q: Do I want to include nrepl here?
            ;; A: No.
            ;; It was a bad idea to be doing it at this layer from
            ;; the beginning, and it's even worse since clojure 1.8
            repl (nrepl-server/start-server :port (cfg/repl-port))]
        {:running (promise)
         :sites started-sites
         :repl repl})
      (error "Missing sites in" server)))

  (defn obsolete-stop
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
               (:sites server)))))

(s/defn description :- SystemMap
  "Go from a map of site descriptions to a description of the system to support them"
  [site-descriptions :- sites/site-map]
  (let [raw-servers (reduce (fn [acc site]
                              (let [dscr (key site)]
                                (assoc acc
                                       dscr
                                       (web/init (assoc
                                                  (val site)
                                                  :descriptor dscr)))))
                            {}
                            site-descriptions)
        servers (map web/map->WebServerGroup raw-servers)]
    (info "Server Descriptions going into the SystemMap:\n" (with-out-str (pprint servers)))
    (cpt/system-map
     :running {:done (promise)}
     :servers servers)))

(s/defn dependencies :- SystemMap
  "Add the dependencies among system description components"
  []
  [])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Public

(s/defn init :- SystemMap
  "This approach is mixing up concerns:
I really have a collection of sites, each with its own handlers/stop fn"
  [site-descriptions :- sites/site-map]
  (let [dscr (description site-descriptions)]
    (info "Initializing System for web server(s):\n" (with-out-str (pprint site-descriptions)))
    (cpt/system-using dscr (dependencies))))
