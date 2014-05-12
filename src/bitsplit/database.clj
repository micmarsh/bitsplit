(ns bitsplit.database
    (:use korma.core
          korma.db)
    (:require [com.stuartsierra.component :as component]))

(declare splits users)


(defrecord Database [host port users splits]
    component/Lifecycle

   (start [this]
        (println "starting the database")
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
        (merge this {
                :users users
                :splits splits
            }))
   (stop [this]
        (println "stopping database")))

(defn new-database [host port]
    (map->Database {:host host :port port}))

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