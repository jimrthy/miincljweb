(ns com.jimrthy.cluster-web.web.routes
  (:require
   [compojure.core :as compojure]
   [compojure.route :as route]
   [com.jimrthy.cluster-web.web.renderer :as renderer]
   [com.jimrthy.cluster-web.shared.util :as util]))

(defn index
  "Really a default handler that should just go away.
  At the very least, it absolutely does not belong here."
  [req]
  (renderer/render-template "index" {:greeting "Bonjour"}))

(defn index2 [req]
  (renderer/render-template "index" {:greeting "Ola"}))

(defn not-found
  "Basic error handler. It's main point is to let me know that I'm missing a route
  that some caller expects."
  [req]
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

(compojure/defroutes alt-routes
"Routes for my second site"
(compojure/GET "/" [] index2)
(route/resources "/")
(route/not-found not-found))
