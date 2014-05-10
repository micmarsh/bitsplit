(ns bitsplit.core
    (:require [clj-btc.core :as btc]))

(def all-unspent 
    #(btc/listunspent 
        :minconf 0
        :maxconf 9999999))

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

(def sample-divisions 
    (let [addresses (map #(% "address") (all-unspent))]
        ))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
