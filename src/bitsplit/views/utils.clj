(ns bitsplit.views.utils
    (:use seesaw.core
          [clojure.core.async :only (chan sub go-loop <!)]))

(def map-list 
    (comp 
        (partial grid-panel :columns 1 :items) 
        map))

(defn get-changes [{changes :changes} type]
    (let [channel (chan)]
        (sub changes type channel)
        channel))

(defn dochan! [channel action!]
    (go-loop [ ]
        (if-let [item (<! channel)]
            (action! item))
        (recur)))

