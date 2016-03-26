(ns miincljweb.server
  (:require
   [com.stuartsierra.component :as component]
   [compojure.handler :as handler]
   ;; Q: Is this really the best option available?
   ;; A major part of the point is avoiding servlets, but
   ;; this seems a little extreme
   [org.httpkit.server :as server]
   [ring.middleware.defaults :as ring-defaults]
   [schema.core :as s]
   [taoensso.timbre :as timbre
    :refer (trace debug info warn error fatal spy with-log-level)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Schema

(s/defrecord WebServer [descriptor :- s/Keyword
                        ;; This next is really the router wrapped in middleware
                        ;; Really shouldn't be making the distinction in here,
                        ;; (between this and router)
                        ;; unless it really does make sense to wrap
                        ;; something like ring/site-defaults around all
                        ;; the routing.
                        ;; It kind of does...but only for extremely simplistic
                        ;; sites.
                        dispatcher
                        port :- s/Int
                        ;; Actually, this is probably just a Ring
                        ;; Handler
                        ;; TODO: Verify that
                        ;; Q: Where did I define thes?
                        router ; :- (s/=> ring/response ring/request)
                        shut-down :- (s/=> s/Any)
                        use-site-defaults :- s/Bool]
  component/Lifecycle
  (start
      [this]
    (info "Starting web server: " descriptor "on port" port)
    (let [dispatcher
          #_(handler/site router)  ; deprecated: use ring/site-defaults instead
          (if-not use-site-defaults
            ;; Caller wanted to specify its own middleware
            router
            (ring-defaults/site-defaults router))
          sd (server/run-server dispatcher
                                {:port port})]
      (trace "Started a site on port " port)
    (assoc this
           :shut-down sd
           :dispatcher dispatcher)))

  (stop
      [this]
    (info "Stopping web server:" descriptor "on port" port)
    (shut-down)
    (assoc this
           :dispatcher nil
           :shut-down nil)))

(s/defrecord WebServerGroup [servers :- [WebServer]]
  component/Lifecycle
  (start
      [this]
    (info "Starting group of servers")
    ;; Make sure the started servers don't get GC'd immediately
    (let [started (map component/start servers)]
      ;; Cope with laziness
      (doseq [s started]
        (info "Started server: " s))
      (assoc this :servers started)))

  (stop
      [this]
    (info "Stopping server group")
    (let [stopped (map component/stop servers)]
      (doseq [s stopped]
        (info "Stopped server: " s))
      (assoc this :servers stopped))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Internal

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Public

(s/defn init :- WebServer
  [{:keys [descriptor
           port s/Int
           router]}]
  (map->WebServer {:descriptor descriptor
                   :port port
                   :router router}))
