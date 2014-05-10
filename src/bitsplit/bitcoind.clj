(ns bitsplit.bitcoind
    (:require [clj-btc.core :as btc]))

(def list-unspent 
    #(btc/listunspent 
        :minconf 0
        :maxconf 9999999))



