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
            (println "setup of walletkit completed")
            (on-coins-received wallet
                (fn [tx prev balance]
                    ; wallet-tx->map looks super handy here
                    (println tx balance)
                    (put! channel nil))))))

(defrecord Bitcoinj [wallet-file]
    Queries
    (addresses [this]
        (->> wallet-file  
             :wallet 
             my-addresses
             (map str)))
    (unspent-amounts [this] { })
    (unspent-channel [this]
        (let [return (chan)
              kit (setup-appkit (:wallet wallet-file) return)]
            (.startAndWait kit)  
            return))
    Operations
    (send-amounts! [this amounts]
        ; won't be too slow if this is
        ; as evented as expected
        (eager-map 
            (fn [[to amount]]
                (send-coins (:wallet wallet-file) to amount))
                    amounts))
    (new-address! [this]
        (let [keypair (create-keypair)
              address (-> keypair ->address str)
              {:keys [wallet file]} wallet-file]
            (add-keypair wallet keypair)
            (.saveToFile wallet file)
            address)))

(defn load-wallet 
    ([] (load-wallet "bitsplit.wallet" true))
    ([name test?] 
        (when test?
            (use-test-net))
        (let [wallet (open-wallet name)
              file (java.io.File. name)
              delay 0
              millis java.util.concurrent.TimeUnit/MILLISECONDS]
            ; (.autosaveToFile wallet
            ;     file delay millis 
            ;         (proxy [com.google.bitcoin.wallet.WalletFiles$Listener]
            ;             [] (onAfterAutoSave [saved] (println "yay saved") 
            ;                     (println (open-wallet saved)))
            ;                (onBeforeAutoSave [saved] ())))
            {:wallet wallet :file file})))

