(ns bitsplit.clients.bitcoinj
    (:use bitcoin.core
          bitsplit.clients.protocol
          [clojure.core.set :only [intersection]]
          [clojure.core.async :only (put! chan)]))

(def eager-map (comp doall map))

(defrecord Bitcoinj [wallet]
    Queries
    (addresses [this]
        (my-addresses wallet))
    (unspent-amounts [this] { })
    (unspent-channel [this]
        (let [return (chan)]
            (on-coins-received wallet
                (fn [tx prev balance]
                    ; just need to check out tx, since this: (intersection (set (my-addresses wallet)) (to-addresses tx)))
                    ; looks okay but doesn't say the amount that went to each
                    ; just check tutorial!
                    (println tx balance)
                    (put! return nil)))
            return))
    Operations
    (send-amounts! [this amounts]
        ; won't be too slow if this is
        ; as evented as expected
        (eager-map 
            (fn [[to amount]]
                (send-coins wallet to amount))
                    amounts))
    (new-address! [this]
        (let [kp (create-keypair)]
            (->address kp))))

(defn new-wallet 
    ([] (new-wallet true))
    ([test?] 
        (create-wallet 
            (if test?
                (testNet)
                (prodNet)))))

