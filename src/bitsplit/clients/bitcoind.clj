(ns bitsplit.clients.bitcoind
    (:use bitcoind.clients.protocol)
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
        (let [unspent-tx (listunspent)
              addresses (account list-addresses set)]
            (->> unspent-tx
                (filter #(contains? addresses (% "address")))
                address-amounts)))
    (send-amounts! [this amounts]
        (btc/sendmany
            :fromaccount account
            :address-amount-maps amounts))
    (new-address! [this]
        (btc/getnewaddress :account account)))
