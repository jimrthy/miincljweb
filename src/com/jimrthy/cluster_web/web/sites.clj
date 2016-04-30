(ns com.jimrthy.cluster-web.web.sites
  (:require
   [com.stuartsierra.component :as cpt]
   [schema.core :as s]
   [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Schema

(def port s/Int)

(def site-description {:domain s/Str
                       :port s/Int
                       ;; Seems like it should be a function that retarns a
                       ;; routing function
                       ;; Something more like
                       ;; (s/=> (s/=> RingResponse RingRequest))
                       :router (s/=> s/Any s/Any)})

(def site-map
  "keys are just some arbitrary identifier

But a front-end server (like nginx or apache) should be forwarding
requests to various ports, based upon the hostname. Nothing else
seems to make as much sense here."
  {s/Keyword site-description})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Internal

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Public

(s/defn ^:always-validate init :- site-description
  "This is really just for consistency"
  [dscr :- site-description]
  site-description)
