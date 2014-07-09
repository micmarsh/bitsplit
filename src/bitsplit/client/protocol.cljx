(ns bitsplit.client.protocol)

(defprotocol Queries
    (addresses [this])
    (unspent-amounts [this])
    (unspent-channel [this]))

(defprotocol Operations
    (new-address! [this])
    (send-amounts! [this amounts]))