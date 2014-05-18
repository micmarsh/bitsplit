(ns bitsplit.handlers
    (:use 
        [bitsplit.calculate :only (save-split delete-split)]
        bitsplit.storage.protocol
        bitsplit.storage.filesystem))

(defrecord BalancedFile [data location persist?]
    
    (finish [this new-splits]
        (when persist?
            (spit location new-splits))
        (assoc this :data new-splits)))

    IStorage
    (all [this]
        (if persist? 
            (try-file location data)
            data))
    (split! [this from to percentage]
        (let [new-splits (save-split data from to percentage)]
            (finish this new-splits))
    (unsplit! [this from to]
        (let [new-splits (delete-split data from to)]
            (finish this new-splits)))

(def storage 
    (map->BalancedFile
        {:data { }
         :location SPLITS_LOCATION
         :persist? false}))

(def save! (partial split! storage))
(def delete! (partial unsplit! storage))

