(ns bitsplit.database
    (:use korma.core
          korma.db)
    (:require [com.stuartsierra.component :as component]))

(declare splits users)

(defdb db (postgres {:db "resources/db/split.db"
           :user "postgres"
           :password ""
           }))
(defentity users
    (database db)
    (entity-fields :name :email :password)
    (has-many splits))
(defentity splits
    (database db)
    (entity-fields :address :percentages)
    (belongs-to users))

(defrecord Tables [db users splits])

(declare somehow-hash)

(defn new-user! [{users :users} user]
    (let [password (:password user)
          hashed (update-in user [:password] somehow-hash)]
        (insert users
            (values user))))

(defn new-address! [{splits :splits} address]
    (insert splits
        (values {:address address
                 :percentages { }})))

(defn update-address! [{splits :splits} address percentages]
    (update splits
        (set-fields {:percentages percentages})
        (where {:address [= address]})))

(def real-db (->Tables db users splits))