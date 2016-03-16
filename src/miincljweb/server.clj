(ns miincljweb.server
  (:require
   [com.stuartsierra.component as cpt]
   ;; Q: Is this really the best option available?
   ;; A major part of the point is avoiding servlets, but
   ;; this seems a little extreme
   [org.httpkit.server :as server]
   [schema.core :as s]
   [taoensso.timbre :as timbre
    :refer (trace debug info warn error fatal spy with-log-level)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Schema

(s/defrecord WebServer [handler  ; something magical from compojure
                        port :- s/Int
                        ;; Router is something from compojure, for now
                        router
                        ;; this is a function, I think
                        shut-down]
  component/Lifecycle
  (start
      [this]
    (let [sd (server/run-server (handler/site router)
                                {:port port})]
    (trace "Started " (:domain description) " on port " port)
    (assoc this
           :shut-down sd
           :handler (handler/site router))))

  (stop
      [this]
    )
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Internal

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Public

(s/defn init
  [{:keys [port]}]
  (map->WebServer {:port port}))
