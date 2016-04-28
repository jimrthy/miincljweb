(ns com.jimrthy.cluster-web.shared.db.core
  "Core database functionality"
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [com.jimrthy.cluster-web.shared.util :as util]
            [hara.event :refer [raise]]
            [schema.core :as s]
            [taoensso.timbre :as log])
  (:import [datomic Datom]
           [datomic.db Db DbId]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Schema
;;; N.B. Database schema definitions belong in admin.
;;; This is definitely for Prismatic-style schema and
;;; the way it interacts with Stuart Sierra Components

(defn protocols []
  ;; TODO: Add the rest
  (s/enum :free :ram :sql))

(defn sql-drivers []
  (s/enum :postgres))

(defn UriDescription []
  {:name s/Str
   :port s/Int
   :protocol (protocols)
   :server s/Str ; TODO: This should really be an IP address
   (s/optional-key :driver) (sql-drivers)
   (s/optional-key :user) s/Str
   (s/optional-key :password) s/Str})

(defmulti build-connection-string :protocol)
(defmulti disconnect :protocol)

;; com.jimrthy.cluster-web.admin.db.platform is trying to reference
;; this.
;; The name isn't wise, but it should be legal. Why is this failing?
(s/defrecord URL [description :- (UriDescription)
                  connection-string :- s/Str]
  component/Lifecycle
  (start
   [this]
   "Main point is to verify that we can connect
Although this also serves to cache the connection"
   (comment (log/debug "Starting up the URL. Description: " (util/pretty description)
                       "with keys:" (keys description)))
   (let [connection-string (build-connection-string description)]
     (when (d/create-database connection-string)
       (log/warn "Created new database"))
     (d/connect connection-string)
     (assoc this :connection-string connection-string)))
  (stop
   [this]
   (disconnect description)
   ;; Can't just dissoc...that would return
   ;; an ordinary map that couldn't be started
   ;; At least, that seems to be what I'm picking up
   ;; from the mailing list
   (assoc this :connection-string nil)))

;;; Printing helpers.
;;; TODO: These really belong in their own namespace.
;;; Or, at least, not in this one
;;; Q: Should they really be calling print??

(defmethod io.aviso.exception/exception-dispatch URL
  [url]
  (print "<#URL" (:connection-string url) ">"))

(defmethod io.aviso.exception/exception-dispatch DbId
  [db-id]
  ;; This is super cheesy. But I need something to keep
  ;; my error reporting about it from throwing another exception
  (print "<#DbId" (str db-id) ">"))

(defmethod io.aviso.exception/exception-dispatch schema.core.Either
  [either]
  ;; It's tough to fathom that this doesn't have a good default printer
  (print "(schema.core/either" (:schemas either) ")"))

;;; TODO: Surely these have already been captured somewhere

;; Q: What's a good way to specify that this might have
;; a length of 2-4?
(defn where-clause []
  [s/Symbol])

(defn datomic-query []
  {:find [s/Symbol]
   (s/optional-key :in) [s/Symbol]
   :where [[(where-clause)]]})

(defn BaseTransaction []
  {:db/id (s/either DbId s/Keyword)})

;; Really just a bunch of attribute/value pairs
(defn UpsertTransaction []
  (into (BaseTransaction) {s/Keyword s/Any}))

;;; Q: What's a good way to represent these?
;; UpsertTransactions get translated into these.
;; Note that retractions cannot be represented as a map
(defn RetractTxn []
  [(s/one (s/enum :db/add :db/retract) "Action")
   ;; s/either is deprecated
   ;; TODO: Switch to s/conditional
   (s/one (s/either s/Int s/Keyword) "Which entity")
   s/Any])

(defn TransactionResult []
  {:db-before Db
   :db-after Db
   :tx-data [Datom]
   :temp-ids {s/Int s/Int}})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Internal

(s/defmethod build-connection-string :free :- s/Str
  [{:keys [db-name host port]
    :or {host "localhost"
         ;; TODO: Verify that this is the default
         port 4334}}]
  (str "datomic:free://" host ":" port "/" db-name))

(s/defmethod build-connection-string :ram :- s/Str
  [{:keys [db-name]}]
  (str "datomic:mem://" db-name))

(s/defn sql-driver :- s/Str
  "Return the string representation of a JDBC driver"
  [driver-key :- sql-drivers]
  (let [ms {:postgres "postgresql"}]
    (ms driver-key)))

(s/defmethod build-connection-string :sql :- s/Str
  [{:keys [db-name port driver user password server]
    :or {port 5432
         user "datomic"
         password "datomic"
         server "localhost"}
    :as descr} :- UriDescription]
  (when-not driver
    (raise :missing-driver))
  ;; Next construct is weird because I've shadowed a builtin
  (str "datomic:sql://" db-name "?jdbc:" (sql-driver driver)
       "://" server ":" port "/datomic?user="
       user "&password=" password))

(s/defmethod disconnect :ram
  [descr :- UriDescription]
  ;; We really don't want to keep a reference around to these
  (let [cxn-str (build-connection-string descr)]
    (d/delete-database cxn-str)))

(s/defn general-disconnect
  "Generally don't want to delete the database

  This should be done when the entire process loses interest
  in the connection.
  Its results are async.

I'm mainly including this on the theory that I might want to switch
to a different connection during a reset, and multiple connections
really aren't legal (and probably won't work)."
  [descr :- UriDescription]
  (-> descr build-connection-string d/connect d/release))

(s/defmethod disconnect :sql
  [descr :- UriDescription]
  (general-disconnect descr))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Public

(s/defn uri-ctor :- URL
  [description :- {:description UriDescription}]
  (map->URL description))

(s/defn q :- [s/Any]
  "Convenience function for querying the database.
Probably shouldn't actually use very often.

In general, we should probably be running queries against database values
using d/q. But this approach does save some typing"
  [query :- datomic-query
   uri :- s/Str]
  (d/q query (-> uri d/connect d/db)))

(s/defn pretend-upsert! :- TransactionResult
  "Re-bind upsert! to this for experimentation
or unit testing

Then again, that would mean making it dynamic, which
seems like a bad idea. If nothing else, I think it
has noticeable performance impact because of the
var lookup"
  [uri :- s/Str
   txns :- [UpsertTransaction]]
  (let [conn (d/connect uri)
        database-value (d/db conn)]
    (d/with database-value txns)))

(s/defn upsert! :- TransactionResult
  [uri :- s/Str
   txns :- [UpsertTransaction]]
  (let [conn (d/connect uri)]
    @(d/transact conn txns)))
