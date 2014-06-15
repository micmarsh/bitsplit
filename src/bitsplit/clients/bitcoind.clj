(ns bitsplit.clients.bitcoind
    (:use bitsplit.clients.protocol)
    (:require [clj-btc.core :as btc]
              [bitsplit.calculate :refer [address-amounts]]
              [clojure.core.async :refer [chan put!]]))


(defmacro thread-loop [& body]
    `(.start (Thread. 
        (fn [] 
            (while true
                ~@body)))))

(defn thread-sleep [minutes]
    (Thread/sleep (* minutes 1000 60)))

(def INTERVAL 0.1);(/ 1 30))

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
    (unspent-channel [this]
        (let [return (chan)]
            (thread-loop
                (thread-sleep INTERVAL)
                (let [unspent (unspent-amounts this)]
                    (when (-> unspent empty? not)
                        (put! return unspent))))
            return)
    (send-amounts! [this amounts]
        (btc/sendmany
            :fromaccount account
            :address-amount-maps amounts))
    (new-address! [this]
        (btc/getnewaddress :account account)))
