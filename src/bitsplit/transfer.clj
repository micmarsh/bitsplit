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
            (btc/sendrawtransaction :hexstring))))

(defn make-transfers! [percentages unspent]
    (let [totals (build-totals percentages unspent)]
          (send-transaction! totals unspent)))
; down here: once u mode calculate a 'lil bit, will be able to use "applied percentages"
; map to add database entries of what happened this round, OR add them + transaction id
; to somewhere that listens to confirm transactions
          
(defn send-coins []
    "use for testing"
    (make-transfers! 
        (sample-data)
        (list-unspent)))
