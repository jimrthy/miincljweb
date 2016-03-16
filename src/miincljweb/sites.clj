(ns miincljweb.sites
  (:require
   [com.stuartsierra.component as cpt]
   [schema.core :as s]
   [taoensso.timbre :as timbre
    :refer (trace debug info warn error fatal spy with-log-level)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Schema

(defn SiteDescription {:domain s/Str
                       :port s/Int
                       ;; Seems like it should be a function that retarns a
                       ;; routing function
                       :router (s/=> s/Any)})

(s/defrecord Site []
  cpt/Lifecycle
  (start
      [this]
    (throw (ex-info "Get this written")))
  (stop
      [this]
    (throw (ex-info "Get this written"))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Internal

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Public

(s/defn init :- [Site]
  [descriptions :- [SiteDescription]]
  ;; Q: How well does Components handle lazy sequences?
  (map map->Sites descriptions))
