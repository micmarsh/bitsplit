(ns bitsplit.utils.calculate)

(def combine-sum (partial apply merge-with +))

(def ONE #+clj 1M #+cljs 1)
(def ZERO #+clj 0M #+cljs 0)

(defn amount-map [tx] {(tx "address") (tx "amount")})
(def address-amounts #(->> % (map amount-map) combine-sum))

(defn multiply-values [divisions total-held]
    (into { }
        (map (fn [[addr per]] [addr (* per total-held)])
        divisions)))

(def divide-payments (partial merge-with multiply-values))

(defn select-map [submap supermap]
    (select-keys supermap (keys submap)))

(defn apply-diff [diff percentages]
    (if (empty? percentages)
        percentages
        (let [divisor (-> percentages count
                          #+clj (java.math.BigDecimal.)
                          #+cljs js/Number)
             to-apply #+clj (with-precision 10 (/ diff divisor))
                      #+cljs (/ diff divisor)]
            (into { }
                (map (fn [[addr number]]
                        [addr (+ to-apply number)])
                    percentages)))))

(defn apply-percentages [percentages amounts]
    (let [relevant-percentages (select-map amounts percentages)]
      (->> amounts
           (select-map percentages)
           (divide-payments relevant-percentages))))

(def build-totals (comp combine-sum vals apply-percentages))

(defn save-percentage [data address percentage]
    {:pre [(<= percentage ONE)]}
    (if (empty? data)
        {address ONE}
        (let [previous (or (data address) ZERO)
              diff (- previous percentage)
              without (dissoc data address)
              new-data (assoc data address percentage)]
            (if (empty? without)
              {address ONE}
              (merge new-data
                  (apply-diff diff without))))))

(defn delete-percentage [data address]
    (let [adjusted (save-percentage data address ZERO)]
        (dissoc adjusted address)))

(defn- return [data from splits]
    (assoc data from splits))

(defn save-split [data from to per]
    {:pre [(<= per ONE)]}
    (let [splits (data from)
          new-splits (save-percentage splits to per)]
        (return data from new-splits)))

(defn delete-split [data from to]
    (let [splits (data from)
          new-splits (delete-percentage splits to)]
        (return data from new-splits)))