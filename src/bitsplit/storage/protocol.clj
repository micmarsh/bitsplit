(ns bitsplit.storage.protocol)

(defprotocol IStorage
    (all [store])
    (unsplit! [store from to])
    (split! [store from to percentage]))
