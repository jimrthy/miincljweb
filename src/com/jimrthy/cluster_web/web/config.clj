(ns com.jimrthy.cluster-web.web.config
  (:require [com.jimrthy.cluster-web.web.routes :as routes]))

(defn sites
  "Return a seq that describes the various sites to run.
Very tempting to mimic SharePoint's farm/app/site/web
architecture."
  []
  (throw (ex-info "Obsolete" {:good-idea? :never}))
  [{:domain "example.com"
    :port 9090
    :router #'routes/main-routes}
   {:domain "foo.com"
    :port 9091
    :router #'routes/alt-routes}])

(defn repl-port
  []
  9092)
