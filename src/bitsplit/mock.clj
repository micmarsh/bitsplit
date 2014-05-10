(ns bitsplit.mock
    (:require [clj-btc.core :as btc]))

(def all-unspent 
    #(btc/listunspent 
        :minconf 0
        :maxconf 9999999))

(defn addr-seq [] (map #(% "address") (all-unspent)))

(defn nrand [start end]
    (-> (- end start)
        rand
        (Math/floor)
        (+ start)))

(def rbool #(-> (rand) (< 0.3)))

(defn random-percentages 
    ([] 
        (let [new-amt (nrand 0 100)]
            (random-percentages 
                [new-amt]
                (- 100 new-amt))))
    ([created remaining]
        (if (or (= remaining 0) (rbool) )
            (conj created remaining)
            (let [new-amt (nrand 0 remaining)]
                (recur 
                    (conj created new-amt)
                    (- remaining new-amt))))))

(defn combine-pair [map [addr per]]
    (if (contains? map addr)
        (let [amount (map addr)]
            (assoc map addr (+ amount per)))
        (assoc map addr per)))

(def pairs->map #(reduce combine-pair { } %))

(defn sample-divisions [addresses]
    (let [;addresses (map #(% "address") (all-unspent))
          infaddr (cycle addresses)
          pairs (map (fn [addr per] [addr (/ per 100)]) infaddr (random-percentages))]
        (pairs->map pairs)))    

(defn sample-data []
    (let [all (addr-seq)]
        (pairs->map (map (fn [addr]
            (let [addresses (set (addr-seq))
                  other (disj addresses addr)]
                  [addr (sample-divisions other)]))
        all))))

