(ns miincljweb.system
  (:require
   [clojure.tools.nrepl.server :refer [start-server stop-server]]
   [compojure.handler :as handler]
   [miincljweb.config :as cfg]
   [miincljweb.routes :as routes]
   [org.httpkit.server :as server])
  (:gen-class))


;;;; Based on Stuart Sierra's workflow
;;;; (thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded)
;;;; Probably stupid and pointless to include that here, but at
;;;; least it's some sort of skeletal
;;;; reminder about how to do things.

(defn init
  []
  ;; FIXME: Switch to using slingshot
  {:shut-down (atom (fn [] (throw (Exception. "Not running"))))
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
    (println "Started " (:domain description) " on port " port)
    (into description
          {
           ;; Q: Is handler worth keeping a reference to this around?
           :handler (handler/site router)
           :shut-down sd})))

(defn start
  "Performs side-effects to initialize system, acquire resources, and start it running.
Returns an updated instance of the system.
Dangerous: if this throws an exception, it could easily lock a resource with no way to
release. Pretty much the only way out then is to restart the JVM."
  [server]
  (let [sites (map start-web-server (cfg/sites))]
    ;; Seems like this might possibly be interesting to keep around
    (reset! (:sites server) sites)
    (reset! (:shut-down server) (comp (map :shut-down sites))))

  (let [repl-port (cfg/repl-port)]
    (reset! (:repl server) (start-server :port repl-port)))

  server)

(defn stop
  "Performs side-effects to stop system and release its resources.
Returns an updated instance of the system.
Dangerous in pretty much exactly the same way as start."
  [server]
  ;; FIXME: Watch for exceptions
  (@(:shut-down server ))
  (stop-server @(:repl server))

  (reset! (:sites server) nil)
  (reset! (:shutdown server) (fn [] (throw (RuntimeException. "Not running"))))
  (reset! (:repl server) nil)

  server)

