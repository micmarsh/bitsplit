(ns bitsplit.transfer
    (:use [bitsplit.bitcoind :only (list-unspent)]
          [bitsplit.calculate :only (build-totals)]
          [bitsplit.mock :only (sample-data)])
    (:require [clj-btc.core :as btc]))
    
(defn filter-unspent [keys unspent]
    (->> unspent
        (map #(select-keys % keys))
        vec))

(def idprint (fn [x] (println x) x))

(defn send-transaction! [totals unspent]
    (let [get-hex #(% "hex")
          tv ["txid" "vout"]
          tx-hashes (filter-unspent tv unspent)]
        (btc/settxfee :amount 0.001M)

        (->> totals
            (btc/createrawtransaction 
                :txids-map tx-hashes
                :addrs-amounts-map)
            (btc/signrawtransaction
                :txinfo (filter-unspent (conj tv "scriptPubKey") unspent)
                :hexstring)
            get-hex
            (btc/sendrawtransaction :hexstring)
            idprint)))

(defn make-transfers! [percentages unspent]
    (let [totals (build-totals percentages unspent)]
          (println totals)
          (send-transaction! totals unspent)))

          
(defn send-coins 
    "use for testing" []
    (let [data (sample-data)
          unspent (list-unspent)]
        (println data)
        (println unspent)
        ; (make-transfers! 
        ;     data
        ;     unspent)
        ))
