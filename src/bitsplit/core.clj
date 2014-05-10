(ns bitsplit.core
    (:use [bitsplit.bitcoind :only (list-unspent)]
          [bitsplit.mock :only (sample-data)])
    (:require [clj-btc.core :as btc]))
    
(defn filter-unspent [keys unspent]
    (map #(select-keys % keys) unspent))

(def amount (partial btc/getreceivedbyaddress :bitcoinaddress ))

(defn convert-total [map]
    {(map "address") (map "amount")})

(defn pairs->map [pairs]
    (reduce #(assoc %1 (%2 0) (%2 1)) { } pairs))


(defn calculate-transactions [total-held divisions]
    (pairs->map (map (fn [[addr per]]
            [addr (* per total-held)]) divisions)))

(defn send-coins []
    (let [unspent (list-unspent)
          tv ["txid" "vout"]
          ; amtot ["amount" "address"]
          tx-hashes (filter-unspent tv unspent)
          amounts (map convert-total unspent)
          totals (apply merge-with + amounts)
          send-amounts (merge-with calculate-transactions
                        totals
                        (sample-data))
          send-totals (apply merge-with + (vals send-amounts))

          first-hex (btc/createrawtransaction 
                        :txids-map (vec tx-hashes)
                        :addrs-amounts-map send-totals)

          signed (btc/signrawtransaction
                        :hexstring first-hex
                        :txinfo (vec (filter-unspent (conj tv "scriptPubKey") unspent)))

          done (btc/sendrawtransaction :hexstring (signed "hex"))

          ]
         done))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
