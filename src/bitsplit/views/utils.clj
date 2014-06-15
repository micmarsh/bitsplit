(ns bitsplit.views.utils
    (:use seesaw.core))

(def map-list 
    (comp 
        (partial vertical-panel :items) 
        map))

(defn insert-second [[head & tail] thing]
    (->> tail
        (cons thing)
        (cons head)))

(defn assoc-second [items thing]
    (let [head (first items)
          tail (-> items rest rest)]
        (insert-second (cons head tail) thing)))
