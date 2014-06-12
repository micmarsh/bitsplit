(ns bitsplit.clients.bitcoind
    (:require [clj-btc.core :as btc]))

(def list-unspent 
    #(btc/listunspent 
        :minconf 1
        :maxconf 9999999))

(defn list-addresses
    ([] (list-addresses ""))
    ([account]
        (btc/getaddressesbyaccount
            :account account)))
