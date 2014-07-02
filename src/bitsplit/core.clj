(ns bitsplit.core
  (:use bitsplit.storage.protocol
        bitsplit.clients.protocol
        [clojure.core.async :only (go put! <!)])
  (:require [bitsplit.handlers :as handlers]
            [bitsplit.transfer :as transfer]
            [bitsplit.utils.calculate :as calc]))

; (defn save! [storage params]
;     (let [{:keys [from to percentage]} params]
;         (->> (java.math.BigDecimal. percentage)
;             (swap! storage split! from to)
;             :data)))

; (defn handle-actions! [storage actions changes]
;     (go (while true
;         (let [action (<! actions)]
;             (condp = (:type action)
;                 :add-address
;                     (put! changes
;                         {:percentages ((save! storage action)
;                                             (:from action))
;                          :from (:from action)
;                          :type :add-address}))))))

(defn add-address! [storage {:keys [parent address percent]}]
    (let [existing (lookup storage parent)
          new-split (calc/save-percentage existing address percent)]
        (save! storage parent new-split)))

(defn remove-address! [storage {:keys [parent address]}])

(defn new-split! 
    ([storage client] (new-split! storage client { }))
    ([storage client {:keys [children]}]))

(defn remove-split! [storage {:keys [split]}])

(defn edit-address! [storage {:keys [parent address percent]}])

(defn list-all [storage])

(defn -main [ & [mode] ]
    (println "sup")) 