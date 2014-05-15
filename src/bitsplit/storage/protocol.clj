(ns bitsplit.storage.protocol)

(defprotocol IStorage
    (new-user! [store user])
    (new-address! [store address])
    (update-address! [store address percentages]))
