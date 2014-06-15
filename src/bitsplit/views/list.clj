(ns bitsplit.views.list
    (:use seesaw.core
          [bitsplit.views.utils :only [map-list assoc-second]]))

(defn percentage->ui [[address percentage]]
    (left-right-split
        (label address)
        (label percentage)))

(def compact (partial filter identity))
(defn register-button [actions address percentage parent button]
    (listen button :action 
        (fn [e]
            (async/put! actions
                {:type :add-address
                 :from parent
                 :to (value address)
                 :percentage (value percentage)})))
    button)

(defn address-adder [actions parent percentage?]
    (let [address (text "")
          percentage (text (if percentage? "" "1"))]
        (flow-panel :items (compact [ 
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

(defn new-addresses [panel percentages]
    (let [panel-items (config panel :items)
          new-items (assoc-second panel-items (map-list percentage->ui percentages))]
        (config! panel :items new-items)))
