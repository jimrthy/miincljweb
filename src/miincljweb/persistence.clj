(ns miincljweb.persistence
  (:require [korma.db :as db]
            [korma.core :as kc]))

;; Really should load this from a configuration file instead.
(db/defdb ^:dynamic *db* (db/sqlite3 {:classname "org.sqlite.JDBC"
                                      :subprotocol "sqlite"
                                      :subname "test.DB"}))

;;; Some example entities inherited from a random database I had
;;; sitting around.
(kc/defentity partner_paymentprocessor
  (kc/entity-fields :name :short_code))

(declare partner_onetwo)

(kc/defentity partner_two
  (kc/entity-fields :name :short_code :production_url :test_url)
  (kc/has-many partner_onetwo {:fk :second_id}))

(kc/defentity partner_one
  (kc/entity-fields :description
                    :short_code
                    :my_id
                    :numeric_code :slug :name)
  (kc/belongs-to partner_paymentprocessor {:fk :payment_processor_id})
  (kc/has-many partner_onetwo {:fk :partner_id}))

;;;Really a many-to-many indexer between channel partners and
;;;lender connections.
(kc/defentity partner_onetwo
  (kc/entity-fields :sequence)
  (kc/belongs-to partner_one {:fk :partner_id})
  (kc/belongs-to partner_two {:fk :second_id}))

(defn get-lenders-for-partner [id]
  (kc/select partner_one 
             (kc/with partner_onetwo 
                      (kc/with partner_two))
             (kc/where {:slug id})))
