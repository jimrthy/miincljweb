(ns miincljweb.system
  (:require
   [clojure.tools.nrepl.server :refer [start-server stop-server]]
   [compojure.core :as compojure]
   [compojure.handler :as handler]
   [compojure.route :as route]
   [miincljweb.renderer :as renderer]
   ;; FIXME: At the very least, move the default routes into here.
   ;;[miincljweb.routing :as routing]
   [miincljweb.util :as util]
   [org.httpkit.server :as server])
  (:gen-class))


;;;; Based on Stuart Sierra's workflow (thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded)
;;;; Probably stupid and pointless to include this here, but at least it's some sort of skeletal
;;;; reminder about how to do things.

(defn init
  []
  ;; FIXME: Switch to using slingshot
  {:shut-down (atom (fn [] (throw (Exception. "Not running"))))
   :repl (atom nil)
   ;; For lein-ring.
   :handler (atom nil)})

;;; FIXME: None of these next few routing pieces belong in here.
(defn index [req]
  "Really a default handler that should just go away.
At the very least, it absolutely does not belong here."
  (renderer/render-template "index" {:greeting "Bonjour"}))

(defn not-found [req]
"Basic error handler. It's main point is to let me know that I'm missing a route
that some caller expects."
  (let [result
        {:status 404
         :headers {"Content-Type" "text/html"}
         :body "Not Found"}]
    (util/log (str "REQUEST: " req "\nRESULT: " result))
    result))

(compojure/defroutes main-routes
"Routing."
  (compojure/GET "/" [] index)
  (route/resources "/")
  (route/not-found not-found))

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
    (let [sd (server/run-server (handler/site #'main-routes) {:port port})]
      (comment (println "Setting up a new web server on port " port " with a shutdown:\n" sd
                        "\nCurrent Shut Down:\n" (:shut-down server)))
      (reset! (:shut-down server) sd))
    (reset! (:repl server) (start-server :port repl-port))

    (reset! (:handler server) (handler/site main-routes))))

(defn stop
  "Performs side-effects to stop system and release its resources.
Returns an updated instance of the system.
Dangerous in pretty much exactly the same way as start."
  [server]
  ;; FIXME: Watch for exceptions
  (@(:shut-down server ))
  (stop-server @(:repl server)))

