(ns com.jimrthy.cluster-web.web.system
  "Skeleton that provides the structure for everything else to build around"
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.tools.nrepl.server :as nrepl-server]
   [clojure.core.reducers :as r]
   [com.jimrthy.cluster-web.web.routes :as routes]
   [com.jimrthy.cluster-web.web.server :as web]
   [com.jimrthy.cluster-web.web.sites :as sites]
   [com.stuartsierra.component :as cpt]
   [schema.core :as s]
   [taoensso.timbre :as timbre
    :refer (trace debug info warn error fatal spy with-log-level)])
  (:import
   [com.stuartsierra.component SystemMap]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Schema

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Internal

(s/defn description :- SystemMap
  "Go from a map of site descriptions to a description of the system to support them"
  [site-descriptions :- sites/site-map]
  (info "Setting up SystemMap description based upon\n" site-descriptions)
  (let [raw-servers (reduce (fn [acc site]
                              (let [dscr (key site)
                                    details (val site)]
                                (trace "reduce'ing" dscr
                                       "\nassociated w/" details)
                                (assoc acc
                                       dscr
                                       (web/init (assoc
                                                  details
                                                  :descriptor dscr)))))
                            {}
                            site-descriptions)
        _ (trace "Initializing Server descriptions based on\n" raw-servers)
        group (web/map->WebServerGroup {:servers raw-servers})]
    (trace "Server Descriptions going into the SystemMap:\n"
          raw-servers
          "\naka\n"
          (with-out-str (pprint raw-servers)))
    (cpt/system-map
     :running {:done (promise)}
     :servers group)))

(s/defn dependencies :- SystemMap
  "Add the dependencies among system description components"
  []
  [])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Public

(s/defn init :- SystemMap
  "This approach is mixing up concerns:
I really have a collection of sites, each with its own handlers/stop fn"
  [site-descriptions :- sites/site-map]
  (let [dscr (description site-descriptions)]
    (info "Initializing System for web server(s):\n" (with-out-str (pprint site-descriptions)))
    (cpt/system-using dscr (dependencies))))
