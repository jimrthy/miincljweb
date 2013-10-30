(ns miincljweb.core
  (:require 
   ;;; Q: Do these still serve some sort of purpose?
   ;;[clojure.xml :as xml]
   ;;[clojure.zip :as zip]
   [miincljweb.config :as cfg]
   [miincljweb.system :as system])
  (:gen-class))


(defn -main
  "Start the server."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))

  (let [dead (system/init)]
    (reset! (:sites dead) (cfg/sites))
    (let [server (system/start dead)]
      (comment 
        (try
          ;; Q: What on earth can I do here?
          ;; A: Honestly, this should probably just exit and
          ;; not worry about trying to clean up. That's pretty much
          ;; the entire point to running this as main, after all.
          (finally
            (system/stop server)))))))
