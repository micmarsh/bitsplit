(ns bitsplit.core
    (:use [bitsplit.bitcoind :only (list-unspent)]
          [bitsplit.mock :only (sample-data)])
    (:require [clj-btc.core :as btc]))
    
(defn filter-unspent [keys unspent]
    (map #(select-keys % keys) unspent))

(def amount (partial btc/getreceivedbyaddress :bitcoinaddress ))

(defn amount-map [tx]
    {(tx "address") (tx "amount")})

(defn pairs->map [pairs]
    (reduce #(assoc %1 (%2 0) (%2 1)) { } pairs))


(defn calculate-transactions [divisions total-held]
    (pairs->map (map (fn [[addr per]]
            [addr (* per total-held)]) divisions)))

(defn build-totals [unspent]
    (->> unspent
         (map amount-map)
         (apply merge-with +)
         (merge-with calculate-transactions (sample-data))
         vals
         (apply merge-with +)))

(defn send-coins []
    (let [unspent (list-unspent)

          send-totals (build-totals unspent)

          tv ["txid" "vout"]
          tx-hashes (filter-unspent tv unspent)
          first-hex (btc/createrawtransaction 
                        :txids-map (vec tx-hashes)
                        :addrs-amounts-map send-totals)

          signed (btc/signrawtransaction
                        :hexstring first-hex
                        :txinfo (vec (filter-unspent (conj tv "scriptPubKey") unspent)))
          feeset (btc/settxfee :amount 0.001M)
          done (btc/sendrawtransaction :hexstring (signed "hex"))]
         done))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
