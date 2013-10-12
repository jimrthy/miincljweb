(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [miincljweb.system :as sys]))

(def system nil)

(defn init
  []
  (alter-var-root #'system
                  (constantly (sys/init))))

(defn start
  []
  (alter-var-root #'system (sys/start)))

(defn stop
  []
  (alter-var-root #'system
                  (fn [s] (when s (sys/stop s)))))

(defn go
  []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))
