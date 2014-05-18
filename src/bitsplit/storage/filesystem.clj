(ns bitsplit.storage.filesystem
    (:use bitsplit.storage.protocol))


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

(def splits (try-file SPLITS_LOCATION))

(defrecord SplitFile [splits location persist?] 
    IStorage
    (all [this]
        (if persist?
            (try-file location splits)
            splits))
    (split! [this from to percentage]
        (let [percentages (get splits from { })
              new-percents (assoc percentages to percentage)
              new-file (assoc splits from new-percents)]
            (when persist? 
                (spit location new-file))
            (assoc this :splits new-file)))
    (unsplit! [this from to]
        (let [percentages (get splits from { })
              new-percents (dissoc percentages to)
              new-file (assoc splits from new-percents)]
            (when persist? 
                (spit location new-file))
            (assoc this :splits new-file))))