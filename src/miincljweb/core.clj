(ns miincljweb.core
  (:require
   [com.stuartsierra.component :as component]
   [compojure.core :as compojure]
   [miincljweb.config :as cfg]
   [miincljweb.system :as system])
  (:gen-class))

(compojure/defroutes example-one
  "One way to define routes"
  (compojure/GET "/" [] (fn [_] {:status 200
                                 :headers {"Content-Type" "text/plain"}
                                 :body "First example"})))

(defn example-two
  "This is just a ring handler, isn't it?"
  [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Another example"})

(defn default-sample
  "Pointless web suite to use for demo/basic testing"
  []
  {:one {:domain "sample.fake.tld"
         :port 16487
         :router example-one}
   :two {:domain "another.fake.tld"
         :port 16488
         :router example-two}})


(defn -main
  "Start the server."
  [& args]
  (println "Initializing System")
  (let [dead (system/init (default-sample))]
    (let [server (component/start dead)]
      (println "System Started")
      (try
        ;;; Provide a mechanism for exit-signalling, just because
        (-> server :running :done deref)
        (println "System marked complete without an error")
        (finally
          (println "Stopping system")
          (component/stop server)
          (println "System stopped"))))))
