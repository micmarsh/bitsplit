(ns bitsplit.views.main
    (:use seesaw.core
        [bitsplit.mock :only (sample-data)])
    (:require [clojure.core.async :as async]))

(def map-list 
    (comp 
        (partial vertical-panel :items) 
        map))

(defn percentage->ui [[address percentage]]
    (left-right-split
        (label address)
        (label percentage)))

(def compact (partial filter identity))

(defn address-adder [actions percentage?]
    (let [address (text "")
          percentage (text (if percentage? "" "1"))]
        (flow-panel :items (compact [ 
            address
            (when percentage? percentage)
            (-> (button :text "Add Address" )
                (listen :action 
                    (fn [e]
                        (async/put! actions
                            {:type :add-address
                             :address (value address)
                             :percentage (value percentage)}))))]))))


(defn entry->ui [actions [address percentages]]
    (vertical-panel :items [
        (label address)
        (map-list percentage->ui percentages)
        (address-adder actions 
            (-> percentages empty? not))]))

(defn splits->ui [actions splits]
    (->> splits
        (map-list (partial entry->ui actions))
        scrollable))

(defn start-ui! [initial changes]
    (let [actions (async/chan)
          data (splits->ui actions initial)
          main (frame
                :size [400 :by 500]
                :title "Bitsplit"
                :content data)]
          (show! main)
          actions))

(def start! #(start-ui! (sample-data) (async/chan)))

