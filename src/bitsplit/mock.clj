(ns bitsplit.mock
    (:use [bitsplit.bitcoind :only (list-unspent)]))

(defn addr-seq [] (map #(% "address") (list-unspent)))

(def set1 #{"ms6dXVXFBfwriUZaLACsRSYku8nc3rQNRe"
            "mt6hkAZtLWCXaWw5GN6CQpq2VF3hjbAtMc"
            "n2rDFfh5uhY7RZg6opEWdKy24cU3ntXAmN"
})
(def set2 #{
        "mzAWJh9tUqUKFrEHFJEAaxXpLrSFnJMEys"
        "mgoZxYMjuZcEvfJK31hnUFCW35hfvcPfaD"
        "mopBDLcHaBZ8Lu32VBFusuHjrbexxngqdt"
        "mpFjBM87wFXERLSkVZLt9awz4kjzBHrtJD"
    })

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
    (if-let [amount (map addr)]
        (assoc map addr (+ amount per)))
    (assoc map addr per))

(def pairs->map #(reduce combine-pair { } %))

(defn sample-divisions [addresses]
    (let [infaddr (cycle addresses)
          pairs (map (fn [addr per] [addr (/ per 100)]) 
                    infaddr (random-percentages))]
        (pairs->map pairs)))    

(defn sample-data []
    (let [all (addr-seq)
          sample (first all)]
        (pairs->map (map (fn [addr]
            (let [addresses (if (set1 sample) set2 set1)
                  other (disj addresses addr)]
                  [addr (sample-divisions other)]))
        all))))

