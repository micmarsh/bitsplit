(ns bitsplit.storage.protocol)

(defprotocol Storage
    (all [store]
        "List every split: {\"split1\" {\"addr1\" percent ...} ...}")
    (lookup [this split]
        "Returns the {\"addr1\" percent ...} listing for a given split")
    (delete! [store address]
        "Remove the given from - to address pair, returning the new set of splits")
    (save! [store address splits]
        "Save the given set of splits and return {\"split\" {\"addr1\" percent ...}}"))
