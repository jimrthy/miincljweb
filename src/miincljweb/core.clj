(ns miincljweb.core
  (:require [org.httpkit.server :as server]
            [compojure.core :as compojure]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [miincljweb.util :as util]
            [miincljweb.renderer :as renderer]
            [miincljweb.routing :as routing])
  (:gen-class))

(defn index [req]
  "Really a default handler that should just go away."
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

;; This isn't exactly elegant, but the basic idea is simple.
;; Stash the server's shutdown function in here. When you're ready to kill it,
;; deref and call it.
;; If you overwrite it, or lose the reference in some other way, it's
;; your fault...though that would be a good argument to come up with a
;; better approach.
(def ^:dynamic *shut-down* (atom (fn [] (throw (Exception. "Not running")))))

(defn -main
  "Start the server."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "9090"))]
    (let [sd (server/run-server (handler/site #'main-routes) {:port port})]
      (swap! *shut-down* (fn [_] sd)))))

;; For lein-ring.
(def app (handler/site main-routes))
