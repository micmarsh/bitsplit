(ns bitsplit.transfer
    (:use bitsplit.clients.protocol
          [bitsplit.calculate :only (build-totals)]
          [bitsplit.mock :only (sample-data)]))
  

(defn make-transfers! [client percentages unspent]
    (->> unspent
         (build-totals percentages)
         (send-amounts! client)))
          