(ns bitsplit.storage.filesystem
    (:use bitsplit.storage.protocol)
    (:require [bitsplit.calculate :as calc]))

(def HOME (System/getProperty "user.home"))
(def DIR (str HOME "/.bitcoin/bitsplit/"))
(.mkdir (java.io.File. DIR))
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

(defprotocol Finishable (finish [this data]))

(defrecord BalancedFile [data location persist?]
    IStorage
    (all [this]
        (if persist? 
            (try-file location data)
            data))
    (split! [this from to percentage]
        (let [new-splits (calc/save-split data from to percentage)]
            (finish this new-splits)))
    (unsplit! [this from to]
        (let [new-splits (calc/delete-split data from to)]
            (finish this new-splits)))

    Finishable
    (finish [this new-splits]
        (when persist?
            (spit location new-splits))
        (assoc this :data new-splits)))
