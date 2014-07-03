(ns bitsplit.fakes
    (:use bitsplit.client.protocol
          bitsplit.storage.protocol))

(defrecord FakeStorage [database-atom]
    Storage
    (all [_] @database-atom)
    (lookup [_ split] (@database-atom split))
    (delete! [_ address] (swap! database-atom dissoc address))
    (save! [_ address splits] 
        (select-keys
            (swap! database-atom 
                assoc address splits)
                [address])))

(defrecord FakeClient [addresses]
    Operations
    (new-address! [this]
        (-> addresses shuffle first))
    (send-amounts! [this _] "blah"))

(def fake-storage (->FakeStorage (atom { })))

(def fake-client 
    (->FakeClient 
        (map #(str "address" %) (range 20))))