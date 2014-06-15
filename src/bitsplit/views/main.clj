(ns bitsplit.views.main
    (:use seesaw.core
        [bitsplit.views.list :only (entry->ui new-addresses)]
        [bitsplit.views.utils :only (map-list)]
        [bitsplit.mock :only (sample-data)])
    (:require [clojure.core.async :refer (go chan <! pub)]))

(defn splits->ui [channels splits]
    (->> splits
        (map-list (partial entry->ui channels))
        scrollable))

(defn start-ui! [initial changes]
    (let [actions (chan)
          channels {:changes (pub changes :type)
                    :actions actions}
          ui (splits->ui channels initial)
          main (frame
                :size [400 :by 500]
                :title "Bitsplit"
                :content ui)]
          (show! main)
          actions))

(def start! #(start-ui! (sample-data) (chan)))

