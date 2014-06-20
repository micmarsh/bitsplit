(ns bitsplit.views.main
    (:use seesaw.core
        [bitsplit.views.list :only (entry->ui)]
        [bitsplit.views.utils :only (map-list)]
        [bitsplit.mock :only (sample-data)])
    (:require [clojure.core.async :refer (go chan <! pub)]))

(defn splits->ui [channels splits]
    (top-bottom-split
        (map-list (partial entry->ui channels) splits)
        (left-right-split
            (text "")
            (button :text "Generate Address")
            :divider-location 4/6)
        :divider-location 5/6))

(defn start-ui! [initial changes]
    (native!)
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
