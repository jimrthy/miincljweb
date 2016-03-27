(ns miincljweb.server
  (:require
   [com.stuartsierra.component :as component]
   [compojure.handler :as handler]
   [miincljweb.ring-schema :as ring-schema]
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
                        router :- ring-schema/handler
                        shut-down :- (s/=> s/Any)
                        use-site-defaults :- s/Bool]
  component/Lifecycle
  (start
      [this]
    (info "Starting web server: " descriptor "on port" port)
    (let [dispatcher
          (if-not use-site-defaults
            ;; Caller wanted to specify its own middleware
            router
            (ring-defaults/wrap-defaults router ring-defaults/site-defaults))
          sd (server/run-server dispatcher
                                {:port port})]
      (trace "Started a site on port " port)
    (assoc this
           :shut-down sd
           :dispatcher dispatcher)))

  (stop
      [this]
    (info "Stopping web server:" descriptor "on port" port)
    (when shut-down
      (shut-down))
    (assoc this
           :dispatcher nil
           :shut-down nil)))

(s/defrecord WebServerGroup [servers :- {s/Keyword WebServer}]
  component/Lifecycle
  (start
      [this]
    (info "Starting group of servers: " servers)
    ;; Make sure the started servers don't get GC'd immediately by starting them
    (assoc this :servers (reduce
                          (fn [acc [k ^WebServer v]]
                            (if v
                              (assoc acc k (.start v))
                              acc))
                          {}
                          servers)))

  (stop
      [this]
    (info "Stopping server group")
    (assoc this :servers (reduce
                          (fn [acc [k ^WebServer v]]
                            (if v
                              (assoc acc k (.stop v))
                              acc))
                          {}
                          servers))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Internal

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Public

(s/defn init :- WebServer
  [{:keys [descriptor
           port  ; s/Int
           router
           use-site-defaults]
    :as opts}]
  (let [args {:descriptor descriptor
              :port port
              :router router
              :use-site-defaults use-site-defaults}]
    (trace "Initializing a web server based on:\n" args
          "\nfrom\n" opts)
    (let [result (map->WebServer args)]
      ;; Q: This isn't going to show anything useful, is it?
      (trace "Initialized WebServer record:\n" result)
      result)))
