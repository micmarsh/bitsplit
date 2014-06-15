(ns bitsplit.handlers
    (:use
        bitsplit.storage.protocol
        [clojure.core.async :only (go <! put!)])
    (:require [bitsplit.calculate :as calc]
              [bitsplit.clients.bitcoind :as rpc]))


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
                        {:percentages ((save! storage action)
                                            (:from action))
                         :from (:from action)
                         :type :add-address}))))))

(def list-all #(-> % deref all))

