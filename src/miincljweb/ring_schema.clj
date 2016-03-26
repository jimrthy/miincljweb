(ns mincljweb.ring-schema
  (:require [com.stuartsierra.component :as component]
            [immutant.web :as web]
            [ribol.core :refer (raise)]
            [schema.core :as s])
  (:import [clojure.lang ISeq]
           [java.io File InputStream]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Schema

(def s-or-ss (s/either s/Str [s/Str]))

(def request {;; According to the Ring spec, body must be a key.
              ;; That doesn't usually match reality.
              :body (s/either s/Str InputStream)
              :headers {s/Any s/Any}
              :query-string s/Str
              :remote-addr s/Str
              :request-method (s/enum :get :head :options :put :post :delete)
              :scheme (s/enum :http :https)
              :server-port s/Int
              :server-name s/Str
              :uri s/Str
              ;; deprecated
              (s/optional-key :content-type) s/Str
              (s/optional-key :content-length) s/Int
              (s/optional-key :character-encoding) s/Str
              ;; Middleware is free to add anything it likes
              s/Any s/Any})

(def response {(s/optional-key :body) (s/either s/Str ISeq File InputStream)
               :headers {s/Str s-or-ss}
               :status s/Int})

(def handler (s/=> response request))