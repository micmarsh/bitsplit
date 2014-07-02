(ns bitsplit.clients.bitcoind
    (:use bitsplit.clients.protocol
          bitsplit.clients.utils)
    (:require [clj-btc.core :as btc]
              [bitsplit.calculate :refer [address-amounts]]
              [clojure.core.async :refer [chan put!]]))

(def INTERVAL 0.1)

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
    Queries
    (addresses [this]
        (list-addresses account))
    (unspent-amounts [this]
        (let [unspent-tx (list-unspent)
              addresses (-> account list-addresses set)
              account-address? #(contains? addresses (% "address"))]
            (->> unspent-tx
                (filter account-address?)
                address-amounts)))
    (unspent-channel [this]
        (let [return (chan)]
            (thread-interval INTERVAL
                (println "checking for unspent coins")
                (let [unspent (unspent-amounts this)]
                    (when (-> unspent empty? not)
                        (println "found some coins" unspent)
                        (put! return unspent))))
            return))
    Operations
    (send-amounts! [this amounts]
        (btc/sendmany
            :fromaccount account
            :address-amount-maps amounts))
    (new-address! [this]
        (btc/getnewaddress :account account)))
