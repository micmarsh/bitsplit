(ns bitsplit.clients.bitcoinj
    (:use bitcoin.core
          clojure.core.async
          bitsplit.clients.protocol))

(def emap (comp doall map))

(defrecord Bitcoinj [wallet]
    BitsplitClient
    (unspent-amounts [this] { })
    (unspent-channel [this]
        (let [return (chan)]
            (on-coins-received wallet
                (fn [tx prev balance]
                    (put! return nil)))))
    (send-many! [this amounts]
        (emap 
            (fn [[to amount]]
                (send-coins wallet to amount))
                    amounts))
    (new-address! [this]
        (let [kp (create-keypair)]
            (kp->address kp))))



