(ns bitsplit.storage.filesystem
    (:use bitsplit.storage.protocol))


(def HOME (System/getProperty "user.home"))
(def DIR (str HOME "/.bitcoin/bitsplit/"))
(.mkdir (java.io.File. DIR))
(def USERS_LOCATION (str DIR "users"))
(def SPLITS_LOCATION (str DIR "splits"))

(defn try-file 
    ([filename]
        (try-file filename { }))
    ([filename default]
        (try 
            (load-file filename)
        (catch java.io.FileNotFoundException e
            ; default should be loaded from rpc,
            ; but oh well
            default))))

(def users (try-file USERS_LOCATION))
(def splits (try-file SPLITS_LOCATION))

(defrecord SplitFile [users splits] 
    IStorage
    (new-user! [this user]
        (let [with-new (assoc users )])))