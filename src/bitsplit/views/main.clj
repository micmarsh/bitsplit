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

(defn address-adder [percentage?]
    (flow-panel :items [ 
        (text "")
        (when percentage? (text ""))
        (button "Add Address")
    ]))


(defn entry->ui [[address percentages]]
    (vertical-panel :items [
        (label address)
        (map-list percentage->ui percentages)
        (address-adder (-> percentages empty? not))
    ]))

(defn splits->ui [splits]
    (->> splits
        (map-list entry->ui)
        scrollable))

(defn start-ui! [initial]
    (let [data (splits->ui initial)
          main (frame
                :size [400 :by 500]
                :title "Bitsplit"
                :content data)]
          (-> main show!)))

(def start! #(start-ui! (sample-data)))

