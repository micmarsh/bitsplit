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
             to-apply (with-precision 10 (/ diff divisor))]
            (into { }
                (map (fn [[addr number]]
                        [addr (+ to-apply number)])
                    percentages)))))

(defn save-percentage [data address percentage]
    {:pre [(<= percentage 1M)]}
    (if (empty? data)
        {address 1M}
        (let [previous (or (data address) 0M)
              diff (- previous percentage)
              without (dissoc data address)
              new-data (assoc data address percentage)]
            (if (empty? without)
              {address 1M}
              (merge new-data
                  (apply-diff diff without))))))

(defn delete-percentage [data address]
    (let [adjusted (save-percentage data address 0M)]
        (dissoc adjusted address)))

(defn- return [data from splits]
    (if (empty? splits)
        (dissoc data from)
        (assoc data from splits)))

(defn save-split [data from to per]
    {:pre [(<= per 1M)]}
    (let [splits (data from)
          new-splits (save-percentage splits to per)]
        (return data from new-splits)))

(defn delete-split [data from to]
    (let [splits (data from)
          new-splits (delete-percentage splits to)]
        (return data from new-splits)))