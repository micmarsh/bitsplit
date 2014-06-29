(ns bitsplit.transfer
    (:use bitsplit.clients.protocol
          bitsplit.storage.protocol
          [clojure.core.async :only (go <!)]
          [bitsplit.calculate :only (build-totals)]))
  
(defn make-transfers! [client percentages unspent]
    (->> unspent
         (build-totals percentages)
         (send-amounts! client)))
          
(defn handle-unspents! [client storage unspents]
    (println (addresses client))
    (go (while true
        (let [unspent (<! unspents)
              percentages (all @storage)]
            (println "woah coins!" unspent)
            (make-transfers! client percentages unspent)))))