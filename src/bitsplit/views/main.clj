(ns bitsplit.views.main
    (:use seesaw.core
        [bitsplit.views.list :only (entry->ui)]
        [bitsplit.mock :only (sample-data)])
    (:require [clojure.core.async :refer [<!] :as async]))

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
    (let [actions (async/chan)
          ui (splits->ui actions initial)
          main (frame
                :size [400 :by 500]
                :title "Bitsplit"
                :content ui)]
          (show! main)
          (async/go (while true
                (let [change (<! changes)]
                    (apply-change ui change))))
          actions))

(def start! #(start-ui! (sample-data) (async/chan)))

