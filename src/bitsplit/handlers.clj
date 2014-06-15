(ns bitsplit.handlers
    (:use
        bitsplit.storage.protocol
        bitsplit.storage.filesystem)
    (:require [bitsplit.calculate :as calc]
              [bitsplit.clients.bitcoind :as rpc]))

(defn mapmap [fn seq & others]
    (into { } (apply map fn seq others)))

(def storage 
    (-> {:data (mapmap (fn [addr] [addr { }]) ["trololololo", "hello"]);;(rpc/list-addresses))
         :location SPLITS_LOCATION
         :persist? false}
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

