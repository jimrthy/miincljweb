(ns user
  (:require [clj-ns-browser.sdoc :refer (sdoc)]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [miincljweb.config :as cfg]
            [miincljweb.system :as sys]
            [taoensso.timbre :as log]))

(def system nil)

(defn init
  []
  (alter-var-root #'system
                  (constantly (sys/init)))
  (log/set-level! :trace))

(defn start
  []
  (reset! (:sites system) (cfg/sites))
  (alter-var-root #'system sys/start))

(defn stop
  []
  (alter-var-root #'system
                  (fn [s] (when s (sys/stop s)))))

(defn go
  []
  (init)
  (start))

(defn reset
  "Heart of this thing. Stop the current application. Reload any changed source files. Reinitialize everything."
  []
  (stop)
  (refresh :after 'user/go))

