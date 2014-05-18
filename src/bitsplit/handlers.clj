(ns bitsplit.handlers
    (:use
        bitsplit.storage.protocol
        bitsplit.storage.filesystem)
    (:require [bitsplit.calculate :as calc]))

(defprotocol Finishable
    (finish [this data]))

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

(def storage 
    (-> {:data { }
         :location SPLITS_LOCATION
         :persist? false}
        map->BalancedFile
        atom))

(defn save! [{params :params}]
    (let [{:keys [from to]} params
          percent (params "percentage")]
        (->> (java.math.BigDecimal. percent)
            (swap! storage split! from to)
            :data
            str)))

(defn delete! [{{:keys [from to]} :params}]
    (-> (swap! storage unsplit! from to)
        :data 
        str))

