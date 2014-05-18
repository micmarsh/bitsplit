(ns bitsplit.calculate)

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

(defn build-totals [percentages unspent]
    (->> unspent
         address-amounts
         (divide-payments percentages)
         vals
         combine-sum))

(defn apply-diff [diff percentages]
    (if (empty? percentages)
        percentages
        (let [divisor (-> percentages count (java.math.BigDecimal.))
              to-apply (/ diff divisor)]
            (into { }
                (map (fn [[addr number]]
                        [addr (+ to-apply number)])
                    percentages)))))