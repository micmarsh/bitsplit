(ns bitsplit.clients.protocol)

(defprotocol BitsplitClient
    (new-address! [this])
    (unspent-amounts [this])
    (send-amounts! [this amounts]))