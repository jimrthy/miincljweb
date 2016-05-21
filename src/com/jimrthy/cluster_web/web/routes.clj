(ns com.jimrthy.cluster-web.web.routes
  (:require [bidi.bidi :as bidi]
            [com.jimrthy.cluster-web.shared.util :as util]
            [com.jimrthy.cluster-web.web.renderer :as renderer]))

(defn index
  "Really a default handler that should just go away.
  At the very least, it absolutely does not belong here."
  [req]
  (renderer/index {:greeting "Bonjour"}))

(defn index2 [req]
  (renderer/index {:greeting "Ola"}))

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

;; Q: How well does bidi handle multiple routes like this?
(def main-route ["/" :index])
(def main-route ["/index.html" :index])
(def alt-route ["/" :index2])
(def alt-route ["/index.html" :index2])
