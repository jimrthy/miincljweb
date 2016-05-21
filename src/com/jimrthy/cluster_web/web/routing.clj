(ns com.jimrthy.cluster-web.web.routing
  (:require [com.jimrthy.cluster-web.shared.util :as util]))

(defmulti dispatch
  (fn [message params]
    :Not-Implemented))

(defmethod dispatch :something [message params]
  (throw (ex-info "Not Implemented" {:message message
                                     :params params})))

(defmethod dispatch :Error [message params]
  (throw (ex-info "FAIL" {:message message
                          :params params})))

(defmethod dispatch :default [message params]
  ;; Really should return a 404.
  ;; FIXME: For development only.
  ;; For all intents and purposes, this almost definitely indicates
  ;; a piece of functionality that hasn't been ported yet.
  (util/log (str "Message: " message "\nParameters: " params))
  (throw (ex-info "Unrecognized Message Type" {:message message
                                               :params params})))
