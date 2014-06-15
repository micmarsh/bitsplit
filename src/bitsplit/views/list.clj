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

(defn address-adder [actions parent percentage?]
    (let [address (text "")
          percentage (text (if percentage? "" "1"))]
        (flow-panel :id (keyword (str parent "-adder"))
          :items (compact [ 
            address
            (when percentage? percentage)
            (->> (button :text "Add Address" )
                (register-button actions address percentage parent))]))))

(defn entry->ui [actions [address percentages]]
    (vertical-panel
       :id (keyword address)
       :items [
        (label address)
        (map-list percentage->ui percentages)
        (address-adder actions address
            (-> percentages empty? not))]))

(def third #(-> % rest second))

(defn show-percentage [address-form]
    (let [panel-items (config address-form :items)
          insert? (= (count panel-items) 2)
          add-percentage (if insert? insert-second assoc-second)]
          (add-percentage panel-items (text ""))))

(defn new-addresses [panel percentages]
    (let [panel-items (config panel :items)
          address-form (third panel-items)
          new-form-items (show-percentage address-form)
          new-items (assoc-second panel-items (map-list percentage->ui percentages))]
        (config! address-form :items new-form-items)
        (config! panel :items new-items)))
