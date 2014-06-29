(ns bitsplit.clients.bitcoinj
    (:use bitcoin.core
          bitsplit.clients.protocol
          [clojure.set :only (intersection)]
          [clojure.core.async :only (put! chan)]))

(def eager-map (comp doall map))

(defn setup-appkit [wallet channel]
    (proxy [com.google.bitcoin.kits.WalletAppKit]
        ; 100% testNet for now
        [(testNet) (java.io.File. ".") "bitsplit"]
        (onSetupCompleted []
            (on-coins-received wallet
                (fn [tx prev balance]
                    ; just need to check out tx, since this: (intersection (set (my-addresses wallet)) (to-addresses tx)))
                    ; looks okay but doesn't say the amount that went to each
                    ; just check tutorial!
                    (println tx balance)
                    (put! channel nil))))))

(defrecord Bitcoinj [wallet]
    Queries
    (addresses [this]
        (my-addresses wallet))
    (unspent-amounts [this] { })
    (unspent-channel [this]
        (let [return (chan)
              kit (setup-appkit wallet return)]
            (.startAndWait kit)  
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
        (let [keypair (create-keypair)
              address (-> keypair ->address str)]
            (add-keypair wallet keypair)
            address)))

(defn load-wallet 
    ([] (load-wallet "bitsplit.wallet" true))
    ([name test?] 
        (when test?
            (use-test-net))
        (let [wallet (open-wallet name)
              file (java.io.File. name)
              delay 500
              millis java.util.concurrent.TimeUnit/MILLISECONDS]
            (.autosaveToFile wallet
                file delay millis nil)
            wallet)))

