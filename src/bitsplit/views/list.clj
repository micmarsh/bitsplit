(ns bitsplit.views.list
    (:use seesaw.core
          bitsplit.views.utils
          [clojure.core.async :only (put!)]))

(defn percentage->ui [[address percentage]]
    (left-right-split
        (label address)
        (label percentage)))

(def compact (partial filter identity))
(defn register-button [actions address percentage parent button]
    (listen button :action 
        (fn [e]
            (put! actions
                {:type :add-address
                 :from parent
                 :to (value address)
                 :percentage (value percentage)})))
    button)

(defn address-adder [channels parent percentage?]
    (let [address (text "")
          percentage (text (if percentage? "" "1"))]
        (flow-panel :id (keyword (str parent "-adder"))
          :items (compact [ 
            address
            (when percentage? percentage)
            (->> (button :text "Add Address" )
                (register-button (:actions channels) address percentage parent))]))))

(defn entry->ui [channels [address percentages]]
  (let [addr-list (map-list percentage->ui percentages)
        changes (get-changes channels :add-address)]
    (dochan! changes 
        (fn [{:keys [percentages from]}]
          (println percentages from)
          (when (= from address)
            (config! addr-list :items
                (map percentage->ui percentages)))))    
    (vertical-panel
       :id (keyword address)
       :items [
        (label address)
        addr-list
        (address-adder channels address
            (-> percentages empty? not))])))
