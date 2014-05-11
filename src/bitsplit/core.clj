(ns bitsplit.core
    (:use [bitsplit.bitcoind :only (list-unspent)]
          [bitsplit.calculate :only (build-totals)])
    (:require [clj-btc.core :as btc]))
    
(defn filter-unspent [keys unspent]
    (->> unspent
        (map #(select-keys % keys))
        vec))

(def idprint (fn [x] (println x) x))

(defn send-coins []
    (let [unspent (list-unspent)

          send-totals (build-totals unspent)

         ;  tv ["txid" "vout"]
         ;  tx-hashes (filter-unspent tv unspent)
         ;  first-hex (btc/createrawtransaction 
         ;                :txids-map (vec tx-hashes)
         ;                :addrs-amounts-map send-totals)

         ;  signed (btc/signrawtransaction
         ;                :hexstring first-hex
         ;                :txinfo (vec (filter-unspent (conj tv "scriptPubKey") unspent)))
         ;  feeset (btc/settxfee :amount 0.001M)
         ;  done (btc/sendrawtransaction :hexstring (signed "hex"))]
         ; done))
        ] send-totals))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
