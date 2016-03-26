(ns miincljweb.core
  (:require
   [com.stuartsierra.component :as component]
   [miincljweb.config :as cfg]
   [miincljweb.system :as system])
  (:gen-class))

(defn -default-sample
  []
  "Pointless web suite to use for demo/basic testing"
  {:one {:domain "sample.fake.tld"
         :port 16487
         :router }
   })


(defn -main
  "Start the server."
  [& args]
  (let [dead (system/init)]
    (reset! (:sites dead) (cfg/sites))
    (let [server (component/start dead)]
      (try
        ;;; Provide a mechanism for exit-signalling, just because
        (-> server :running :done deref)
        (finally
          (component/stop server))))))
