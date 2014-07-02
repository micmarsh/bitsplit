(ns bitsplit.core
  (:use bitsplit.storage.protocol
        bitsplit.clients.protocol
        [clojure.core.async :only (go put! <!)])
  (:require [bitsplit.handlers :as handlers]
            [bitsplit.transfer :as transfer]))

(defn save! [storage params]
    (let [{:keys [from to percentage]} params]
        (->> (java.math.BigDecimal. percentage)
            (swap! storage split! from to)
            :data)))

(defn handle-actions! [storage actions changes]
    (go (while true
        (let [action (<! actions)]
            (condp = (:type action)
                :add-address
                    (put! changes
                        {:percentages ((save! storage action)
                                            (:from action))
                         :from (:from action)
                         :type :add-address}))))))s

(defn add-address [storage {:keys [parent address percent]}])

(defn remove-address [storage {:keys [parent address]}])

(defn new-split [storage client {:keys [children]}])

(defn remove-split [storage {:keys [split]}])

(defn edit-address [storage {:keys [parent address percent]}])

(defn list-all [storage])

(defn -main [ & [mode] ]
    (println "sup")) 