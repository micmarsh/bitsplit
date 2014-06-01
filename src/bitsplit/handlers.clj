(ns bitsplit.handlers
    (:use
        bitsplit.storage.protocol
        bitsplit.storage.filesystem)
    (:require [bitsplit.calculate :as calc]
              [bitsplit.bitcoind :as rpc]))

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

(defn mapmap [fn seq & others]
    (into { } (apply map fn seq others)))

(def storage 
    (-> {:data (mapmap (fn [addr] [addr { }]) ["trololololo", "hello"]);;(rpc/list-addresses))
         :location SPLITS_LOCATION
         :persist? true}
        map->BalancedFile
        atom))

(defn list-all [req] (-> @storage all str))

(defn save! [{params :params}]
    (let [{:keys [from to]} params
          percent (params "percentage")]
        (println @storage)
        (->> (java.math.BigDecimal. percent)
            (swap! storage split! from to)
            :data
            str)))

(defn delete! [{{:keys [from to]} :params}]
    (-> (swap! storage unsplit! from to)
        :data 
        str))

