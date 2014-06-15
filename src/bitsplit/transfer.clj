(ns bitsplit.transfer
    (:use bitsplit.clients.protocol
          [clojure.core.async :only (go <!)]
          [bitsplit.calculate :only (build-totals)]))
  
(defn make-transfers! [client percentages unspent]
    (->> unspent
         (build-totals percentages)
         (send-amounts! client)))
          

(defn handle-unspents! [client storage unspents]
    (go (while true
        (let [unspent (<! unspents)
              percentages (all @storage)]
            (make-transfers! client percentages unspent)))))