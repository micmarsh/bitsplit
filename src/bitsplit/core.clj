(ns bitsplit.core
  (:use  bitsplit.storage.filesystem
        bitsplit.clients.protocol)
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
                         :type :add-address}))))))

(defn -main [ & [mode] ]
    (println "sup")) 