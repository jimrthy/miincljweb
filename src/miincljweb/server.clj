(ns miincljweb.server
  (:require
   [com.stuartsierra.component :as cpt]
   [compojure.handler :as handler]
   ;; Q: Is this really the best option available?
   ;; A major part of the point is avoiding servlets, but
   ;; this seems a little extreme
   [org.httpkit.server :as server]
   [schema.core :as s]
   [taoensso.timbre :as timbre
    :refer (trace debug info warn error fatal spy with-log-level)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Schema

(s/defrecord WebServer [dispatcher
                        port :- s/Int
                        ;; Router is something from compojure, for now
                        ;; TODO: Really should just be a
                        ;; function that returns...what?
                        ;; Well, nothing magical from compojure
                        router
                        ;; this is a function, I think
                        shut-down]
  component/Lifecycle
  (start
      [this]
    (let [dispatcher (handler/site router)
          sd (server/run-server dispatcher
                                {:port port})]
    (trace "Started " (:domain description) " on port " port)
    (assoc this
           :shut-down sd
           :dispatcher dispatcher)))

  (stop
      [this]
    (shut-down)
    (assoc this
           :dispatcher nil
           :shut-down nil)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Internal

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Public

(s/defn init
  [{:keys [port router]}]
  (map->WebServer {:port port
                   :router router}))
