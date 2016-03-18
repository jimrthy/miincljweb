(ns miincljweb.sites
  (:require
   [com.stuartsierra.component as cpt]
   [schema.core :as s]
   [taoensso.timbre :as timbre
    :refer (trace debug info warn error fatal spy with-log-level)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Schema

(def port s/Int)

(def site-description {:domain s/Str
                       ;; Seems like it should be a function that retarns a
                       ;; routing function
                       ;; Something more like
                       ;; (s/=> (s/=> RingResponse RingRequest))
                       :router (s/=> s/Any)})

(def site-map {port site-description})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Internal

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Public

(s/defn init
  [descriptions :- site-map]
  ;; Q: How well does Components handle lazy sequences?
  ;; A: It doesn't. This approach is all wrong.
  #_(map map->Sites descriptions)
  (throw (ex-info "This is only useful for the schema" {})))
