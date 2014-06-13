(ns bitsplit.transfer
    (:use bitsplit.client.protocol
          [bitsplit.calculate :only (build-totals)]
          [bitsplit.mock :only (sample-data)]))
    
(defn filter-unspent [keys unspent]
    (->> unspent
        (map #(select-keys % keys))
        vec))

(def idprint (fn [x] (println x) x))

(defn send-transaction! [client totals]
    (send-amounts client totals))

(defn make-transfers! [client percentages unspent]
    (->> unspent
         (build-totals percentages)
         (send-transaction! client)))
          
(defn send-coins 
    "use for testing" []
    (let [data (sample-data)
          unspent (list-unspent)]
        (println data)
        (println unspent)
        ; (make-transfers! 
        ;     data
        ;     unspent)
        ))
