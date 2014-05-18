(ns bitsplit.storage-test
  (:use midje.sweet)
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]

            [bitsplit.storage.filesystem :as f]
            [bitsplit.storage.protocol :as p]))

(def storage (f/map->SplitFile {:splits { } :persist? false}))

(fact "storage starts empty"
    (p/all storage) => { })

(def new-storage
    (-> storage
        (p/split! "address1" "address2" 0.9)
        (p/split! "address3" "address4" 0.1)))

(fact "can assoc as expected"
    (p/all new-storage) => {
        "address1" {"address2" 0.9}
        "address3" {"address4" 0.1}
        })

(def newer-storage
    (-> storage
        (p/unsplit! "address1" "address2")
        (p/split! "address3" "address1" 0.3)))

(fact "can clean up and modify values as expected"
    (p/all newer-storage) => {
        "address3" {"address1" 0.3}
        })