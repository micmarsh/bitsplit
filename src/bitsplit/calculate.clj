(ns bitsplit.calculate
    (:use [bitsplit.mock :only (sample-data)]))

(defn amount-map [tx]
    {(tx "address") (tx "amount")})

(defn apply-percentages [divisions total-held]
    (into { } 
        (map (fn [[addr per]]
                [addr (* per total-held)])
        divisions)))

(def combine-sum (partial apply merge-with +))

(def address-amounts 
    (comp 
        combine-sum
        (partial map amount-map)))

(defn build-totals [unspent]
    (->> unspent
         address-amounts
         (merge-with apply-percentages (sample-data))
         vals
         combine-sum))