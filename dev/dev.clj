(ns dev
  "Central part of Stuart Sierra Workflow Revisited

There are very few reasons for your REPL to leave this namespace,
once you have your system compiled and you're ready to go."
  (:require [clojure.core.async :as async]  ; Don't need this yet, but it's indispensable
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [com.jimrthy.cluster-web.web.core :as mwc]
            [com.jimrthy.cluster-web.web.system :as sys]
            [com.stuartsierra.component :as component]
            [schema.core :as s]
            [taoensso.timbre :as log]))

(def system nil)

(defn init
  "Constructs the current development system."
  []
  (alter-var-root #'system
                  (constantly (sys/init (mwc/default-sample))))
  (log/set-level! :trace))

(defn start
  "Starts the current development system."
  []
  (alter-var-root #'system component/start))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system
    (fn [s] (when s (component/stop s)))))

(defn go
  "Initializes the current development system and starts it running."
  []
  (println "Initializing system")
  (init)
  (println "Restarting system")
  (start))

(defn reset
  "Heart of this thing.
  Stop the current application. Reload any changed source files.
  Reinitialize everything."
  []
  (println "Stopping")
  (stop)
  (println "Refreshing namespaces")
  (refresh :after 'dev/go))
