(ns bitsplit.clients.bitcoinj
    (:use bitcoin.core
          bitsplit.clients.protocol
          [clojure.core.async :only (put! chan)]))

(def emap (comp doall map))

(defrecord Bitcoinj [wallet]
    BitsplitClient
    (addresses [this]
        (my-addresses wallet))
    (unspent-amounts [this] { })
    (unspent-channel [this]
        (let [return (chan)]
            (on-coins-received wallet
                (fn [tx prev balance]
                    (println tx balance)
                    (put! return nil)))
            return))
    (send-amounts! [this amounts]
        ; won't be too slow if this is
        ; as evented as expected
        (emap 
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

