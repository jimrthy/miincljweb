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
            [com.stuartsierra.component :as component]
            [miincljweb.config :as cfg]
            [miincljweb.system :as sys]
            [schema.core :as s]
            [taoensso.timbre :as log]))

(def system nil)

(defn init
  "Constructs the current development system."
  []
  (alter-var-root #'system
                  (constantly (system/init {})))
  (log/set-level! :trace))

(defn start
  "Starts the current development system."
  []
  ;; This was a cheese-ball short-cut to get the minimalist
  ;; idea working the first time around.
  ;; TODO: Make this next line go away.
  #_(reset! (:sites system) (cfg/sites))
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

(defn reset []
  "Heart of this thing. Stop the current application. Reload any changed source files. Reinitialize everything."
  (println "Stopping")
  (stop)
  (println "Refreshing namespaces")
  (refresh :after 'dev/go))
