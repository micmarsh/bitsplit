(ns bitsplit.views.main
    (:use seesaw.core
        [bitsplit.mock :only (sample-data)])
    (:require [clojure.core.async :refer [<!] :as async]))

(def map-list 
    (comp 
        (partial vertical-panel :items) 
        map))

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

(defn splits->ui [actions splits]
    (->> splits
        (map-list (partial entry->ui actions))
        scrollable))

(defn add-address [panel address percentage]
    (let [list-items (second (config panel :items))
          new-items (conj list-items (percentage->ui [address percentage]))]
        (config! panel :items new-items)))

(defn apply-change [root change]
    (condp = (:type change)
        :add-address
            (let [{:keys [from to percentage]} change]
                (-> (select root [(keyword (str \# from))])
                    (add-address to percentage)))))

(defn start-ui! [initial changes]
    (let [actions (async/chan)
          ui (splits->ui actions initial)
          main (frame
                :size [400 :by 500]
                :title "Bitsplit"
                :content ui)]
          (show! main)
          (async/go (while true
                (let [change (<! changes)]
                    (apply-change ui change))))
          actions))

(def start! #(start-ui! (sample-data) (async/chan)))

