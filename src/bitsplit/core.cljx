(ns bitsplit.core
  (:use
    [cljs.core.async :only (put! <! chan #+clj go-loop)])
  #+cljs
  (:use-macros
      [cljs.core.async.macros :only (go-loop)])
  (:require [bitsplit.utils.calculate :as calc]
             [bitsplit.storage.protocol :as store]
            [bitsplit.client.protocol :as daemon]))

(defn- modify-address! [modifier storage {:keys [parent address percent]}]
    (let [existing (store/lookup storage parent)
          adjusted (if percent
                      (modifier existing address percent)
                      (modifier existing address))]
          (store/save! storage parent adjusted)))

(def add-address! (partial modify-address! calc/save-percentage))

(def remove-address! (partial modify-address! calc/delete-percentage))

(defn new-split! [storage client]
    (let [address (daemon/new-address! client)]
        (store/save! storage address { })
        address))

(defn remove-split! [storage {:keys [split]}]
    (store/delete! storage split)
    split)

(def edit-address! add-address!) ; maybe in the future each of these
; can throw exceptions if they're not actually called the right way

(def chan?
  (let [chan-type (type (chan))]
    (fn [thing] (= (type thing) chan-type))))

(defn handle-unspents! [builder {:keys [client storage]}]
  (let [unspent-channel (daemon/unspent-channel client)
        results (chan)]
    (go-loop [unspent (<! unspent-channel)]
      (when-let [result (->> unspent
                        (builder (store/all storage))
                        (daemon/send-amounts! client))]
        (if (chan? result)
          (when-let [thing (<! result)]
            (put! results thing))
          (put! results result)))
      (recur (<! unspent-channel)))
    results))
