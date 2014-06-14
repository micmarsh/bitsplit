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

(defn entry->ui [[address percentages]]
    (top-bottom-split
        (label address)
        (map-list percentage->ui percentages)))

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

