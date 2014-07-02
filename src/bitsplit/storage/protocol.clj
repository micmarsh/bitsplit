(ns bitsplit.storage.protocol)

(defprotocol Storage
    (all [store]
        "List every split: {\"split1\" {\"addr1\" percent ...} ...}")
    (unsplit! [store from to]
        "Remove the given from - to address pair, returning the new split or an empty map")
    (split! [store from to percentage]
        "Add the given from - to pair, returning the new split"))
