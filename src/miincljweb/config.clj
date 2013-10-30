(ns miincljweb.config
  (:require [miincljweb.routes :as routes]))

(defn sites 
  "Return a seq that describes the various sites to run.
Very tempting to mimic SharePoint's farm/app/site/web
architecture."
  []
  [{:domain "example.com"
    :port 9090
    :router #'routes/main-routes}
   {:domain "foo.com"
    :port 9091
    :router #'routes/alt-routes}])

(defn repl-port
  []
  9092)
