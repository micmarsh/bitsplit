(ns bitsplit.views.utils
    (:use seesaw.core
          [clojure.core.async :only (chan sub go-loop <!)]))

(def map-list 
    (comp 
        (partial grid-panel :columns 1 :items) 
        map))

(defn insert-second [[head & tail] thing]
    (->> tail
        (cons thing)
        (cons head)))

(defn get-changes [{changes :changes} type]
    (let [channel (chan)]
        (sub changes type channel)
        channel))

(defn dochan! [channel action!]
    (go-loop [ ]
        (if-let [item (<! channel)]
            (action! item))
        (recur)))

(defn assoc-second [items thing]
    (let [head (first items)
          tail (-> items rest rest)]
        (insert-second (cons head tail) thing)))
