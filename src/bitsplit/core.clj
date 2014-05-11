(ns bitsplit.core
    (:use [bitsplit.bitcoind :only (list-unspent)]
          [bitsplit.mock :only (sample-data)])
    (:require [clj-btc.core :as btc]))
    
(defn filter-unspent [keys unspent]
    (->> unspent
        (map #(select-keys % keys))
        vec))

(def amount (partial btc/getreceivedbyaddress :bitcoinaddress ))

(defn amount-map [tx]
    {(tx "address") (tx "amount")})

(defn calculate-transactions [divisions total-held]
    (into { } 
        (map (fn [[addr per]]
                [addr (* per total-held)])
        divisions)))

(def combine-sum (partial apply merge-with +))

(def address-amounts 
    (comp 
        combine-sum
        (partial map amount-map)))

(def idprint (fn [x] (println x) x))

(defn build-totals [unspent]
    (->> unspent
         address-amounts
         (merge-with calculate-transactions (sample-data))
         vals
         combine-sum))

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
