(ns bitsplit.state
    (:require 
          [reagent.core :as r]
          [cljs.reader :refer [read-string]]
          [cljs.core.async :refer [map< take! <! chan]]
          [fluyt.requests :as requests])
    (:use-macros [cljs.core.async.macros :only [go]]))

(def print #(.log js/console %))

(defn flip [function] #(function %2 %1))

(def all-splits (r/atom { }))

(defn receive-splits [response]
    (->> response
        (map< (comp read-string :body))
        ((flip take!)
            (fn [result]
                (print result)
                (reset! all-splits result))))) 

((comp receive-splits requests/get)
    "http://localhost:3000/splits" )

(defn add-address [from to percent]
    ((comp receive-splits requests/post) 
        (str "http://localhost:3000/splits/"
              from "/" to "?percentage=" percent)))

(def new-splits (chan))
(go
    (while true
        (let [{from :from to :address percent :percent}
                 (<! new-splits)]
            ; (print from to percent)
            (add-address from to (or percent 1)))))