(ns bitsplit.crud
    (:require [clj-btc.core :as btc]))

(defn generate-address! [user db rpc] ;db + rpc are deps, user is an input
    "experiment in declaring all dependencies"
    (let [id (:id user)
          address (rpc/getnewaddress :account id)] ; rpc component is pretty freestanding
                                                   ; doesn't depend any anything other than
                                                   ; some hypothetical future :config map
          (save-address! db address user) ; could be part of some "authedDB" 
                                          ; component that needs a user
                                          ; seems like some object generated dynamically
                                          ; from a db component + a user object each time
          address))

(defn modify-split! [{:keys [from to percent] :as split} user db] ;same deal w/ DB + user as above
    (let [current (get-split db from user) ; yup autheddb
          changed (recalculate current split)] ;pure function!
          (save-split! db changed user) ; moar autheddb
          changed))