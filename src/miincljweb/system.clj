(ns miincljweb.system
  (:require
   [clojure.tools.nrepl.server :refer [start-server stop-server]]
   [compojure.handler :as handler]
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
   :repl (atom nil)
   ;; For lein-ring.
   :handler (atom nil)})

;;; FIXME: None of these next few routing pieces belong in here.
(defn start
  "Performs side-effects to initialize system, acquire resources, and start it running.
Returns an updated instance of the system.
Dangerous: if this throws an exception, it could easily lock a resource with no way to
release. Pretty much the only way out then is to restart the JVM."
  [server]
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "9090"))
        ;; The arbitrariness of the next line is ridiculous.
        ;; FIXME: This stuff needs to be in a config namespace
        repl-port (inc port)]
    (let [sd (server/run-server (handler/site #'routes/main-routes) {:port port})]
      (println "Starting a web server on port " port)
      (reset! (:shut-down server) sd))
    (reset! (:repl server) (start-server :port repl-port))

    (reset! (:handler server) (handler/site routes/main-routes))))

(defn stop
  "Performs side-effects to stop system and release its resources.
Returns an updated instance of the system.
Dangerous in pretty much exactly the same way as start."
  [server]
  ;; FIXME: Watch for exceptions
  (@(:shut-down server ))
  (stop-server @(:repl server)))

