(ns bitsplit.views.utils
    (:use seesaw.core))

(def map-list 
    (comp 
        (partial vertical-panel :items) 
        map))

(defn assoc-second [items thing]
    (let [head (first items)
          tail (-> items rest rest)]
        (->> tail
            (cons thing)
            (cons head))))