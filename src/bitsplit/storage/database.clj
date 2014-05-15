(ns bitsplit.storage.database
    (:use bitsplit.storage.protocol
          korma.core
          korma.db))

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

(declare somehow-hash)

(defrecord SplitSQL [db users splits]
    IStorage
    (new-user! [this user]
      (let [password (:password user)
            hashed (update-in user [:password] somehow-hash)]
          (insert users
              (values user))))
    (new-address! [this address]
        (insert splits
            (values {:address address
                     :percentages { }})))
    (update-address! [this address percentages]
        (update splits
            (set-fields {:percentages percentages})
            (where {:address [= address]}))))
