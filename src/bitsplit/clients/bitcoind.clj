(ns bitsplit.clients.bitcoind
    (:use bitsplit.clients.protocol)
    (:require [clj-btc.core :as btc]
              [bitsplit.calculate :refer [address-amounts]]))

(def list-unspent 
    #(btc/listunspent 
        :minconf 1
        :maxconf 9999999))

(defn list-addresses
    ([] (list-addresses ""))
    ([account]
        (btc/getaddressesbyaccount
            :account account)))

(defrecord Bitcoind [account]
    BitsplitClient
    (unspent-amounts [this]
        (let [unspent-tx (list-unspent)
              addresses (-> account list-addresses set)
              account-address? #(contains? addresses (% "address"))]
            (->> unspent-tx
                (filter account-address?)
                address-amounts)))
    (send-amounts! [this amounts]
        (btc/sendmany
            :fromaccount account
            :address-amount-maps amounts))
    (new-address! [this]
        (btc/getnewaddress :account account)))
