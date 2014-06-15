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

(defn form-items [channels parent percentage?]
    (let [address (text :columns 20)
          percentage (text :columns 3 :text (if percentage? "" "1"))]
          (compact [ 
            address
            (when percentage? percentage)
            (->> (button :text "Add Address" )
                (register-button (:actions channels) address percentage parent))])))
(defn address-adder [channels parent percentage?]
    (flow-panel
          :items (form-items channels parent percentage?)))

(defn entry->ui [channels [address percentages]]
  (let [addr-list (map-list percentage->ui percentages)
        address-form (address-adder channels address (-> percentages empty? not))
        changes (get-changes channels :add-address)
        errors (get-changes channels :adding-error)]
    (dochan! changes 
        (fn [{:keys [percentages from]}]
          (when (= from address)
            (config! addr-list :items
                (map percentage->ui percentages))
            (config! address-form :items
                (form-items channels address (-> percentages empty? not)))
            )))
    (dochan! errors
        (fn [{errors :errors}]
            ))    
    (vertical-panel
       :items [
        (label :text address :h-text-position :left)
        addr-list
        address-form])))
