(ns bitsplit.handlers
    (:use
        bitsplit.storage.protocol
        bitsplit.storage.filesystem
        [clojure.core.async :only (go <! put!)])
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

(defn save! [storage params]
    (let [{:keys [from to percentage]} params]
        (println @storage)
        (->> (java.math.BigDecimal. percentage)
            (swap! storage split! from to)
            :data)))

(defn handle-actions! [storage actions changes]
    (go (while true
        (let [action (<! actions)]
            (condp = (:type action)
                :add-address
                    (put! changes
                        ((save! storage action)
                            (:from action))))))))


(defn list-all [req] (all @storage))

(defn delete! [{{:keys [from to]} :params}]
    (-> (swap! storage unsplit! from to)
        :data 
        str))

