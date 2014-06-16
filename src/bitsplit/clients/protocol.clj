(ns bitsplit.clients.protocol)

(defprotocol BitsplitClient
    (addresses [this])
    (new-address! [this])
    (unspent-amounts [this])
    (unspent-channel [this])
    (send-amounts! [this amounts]))