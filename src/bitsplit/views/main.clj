(ns bitsplit.views.main
    (:use seesaw.core
        [bitsplit.views.list :only (entry->ui new-addresses)]
        [bitsplit.views.utils :only (map-list)]
        [bitsplit.mock :only (sample-data)])
    (:require [clojure.core.async :refer (go chan <!)]))

(defn splits->ui [actions splits]
    (->> splits
        (map-list (partial entry->ui actions))
        scrollable))

(defn apply-change [root change]
    (condp = (:type change)
        :add-address
            (let [ {:keys [percentages from]} change]
                (-> (select root [(keyword (str \# from))])
                    (new-addresses percentages)))))

(defn start-ui! [initial changes]
    (let [actions (chan)
          ui (splits->ui actions initial)
          main (frame
                :size [400 :by 500]
                :title "Bitsplit"
                :content ui)]
          (show! main)
          (go (while true
                (let [change (<! changes)]
                    (apply-change ui change))))
          actions))

(def start! #(start-ui! (sample-data) (chan)))

