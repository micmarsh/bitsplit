(ns bitsplit.calculate
    (:use [bitsplit.mock :only (sample-data)]))

(def combine-sum (partial apply merge-with +))

(defn amount-map [tx]
    {(tx "address") (tx "amount")})
(def address-amounts 
    (comp 
        combine-sum
        (partial map amount-map)))

(defn apply-percentages [divisions total-held]
    (into { } 
        (map (fn [[addr per]]
                [addr (* per total-held)])
        divisions)))

(def divide-payments (partial merge-with apply-percentages))

(defn build-totals [unspent]
    (->> unspent
         address-amounts
         (divide-payments (sample-data))
         vals
         combine-sum))