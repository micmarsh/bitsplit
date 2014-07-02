(ns bitsplit.utils.mock)

(def set1 #{"ms6dXVXFBfwriUZaLACsRSYku8nc3rQNRe"
            "mt6hkAZtLWCXaWw5GN6CQpq2VF3hjbAtMc"
            "n2rDFfh5uhY7RZg6opEWdKy24cU3ntXAmN"
            ; returning some testnet coins!
            ; "msj42CCGruhRsFrGATiUuh25dtxYtnpbTx"
})

(defn addr-seq [] set1 );(map #(% "address") (list-unspent)))

(def set2 #{
        "mzAWJh9tUqUKFrEHFJEAaxXpLrSFnJMEys"
        "mgoZxYMjuZcEvfJK31hnUFCW35hfvcPfaD"
        "mopBDLcHaBZ8Lu32VBFusuHjrbexxngqdt"
        "mpFjBM87wFXERLSkVZLt9awz4kjzBHrtJD"
        ; my darkwallet
        "n4PNeR1iSDod7DDJgTWmVznVMfHGKhBqjJ"
    })

(defn nrand [start end]
    (-> (- end start)
        rand
        (Math/floor)
        (+ start)))

(def rbool #(-> (rand) (< 0.3)))

(defn random-percentages [total]
    (loop [created []
           remaining 100]
        (if (= (count created) ( - total 1))
            (->> (conj created remaining)
                (map #(java.math.BigDecimal. %))
                shuffle)
            (let [new-amt (nrand 0 (-> remaining (* 2) (/ 3)))]
                (recur 
                    (conj created new-amt)
                    (- remaining new-amt))))))

(defn combine-pair [map [addr per]]
    (if-let [amount (map addr)]
        (assoc map addr (+ amount per))
        (assoc map addr per)))

(def pairs->map #(reduce combine-pair { } %))

(defn sample-divisions [addresses]
    (let [pairs (map (fn [addr per] [addr (/ per 100M)]) 
                    addresses 
                    (-> addresses count random-percentages))]
        (pairs->map pairs)))    

(defn sample-data []
    (let [all (addr-seq)
          sample (first all)
          pairs (map (fn [addr]
            (let [addresses (if (set1 sample) set2 set1)
                  other (disj addresses addr)]
                  [addr (sample-divisions other)])) all)]
          (into { } pairs)))

