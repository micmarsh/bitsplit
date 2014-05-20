(ns bitsplit.main
    (:require [reagent.core :as r]
              [cljs.reader :refer [read-string]]
              [cljs.core :as c]
              [cljs.core.async :refer [take! map<]]
              [fluyt.requests :as requests]))

(def print #(.log js/console %))
(defn flip [function]
    (fn [arg0 arg1]
        (function arg1 arg0)))

(def all-splits (r/atom { }))
    
(->> (requests/get "http://localhost:3000/splits")
    (map< (comp read-string :body))
    ((flip take!) 
        (fn [result]
            (print result)
            (reset! all-splits result))))

(defn splits-view [splits]
    (for [[to percentage] splits]
        ^{:key to}
        [:p 
            [:span to] ": "
            [:span percentage]]))

(defn insert-new [new-channels]
    (let [values (atom { })]
        [:div
            [:input {:placeholder "Split to new address"
                     :on-change #(swap! values 
                        assoc :address (-> % .-target .-value))}]
            [:input {:on-change #(swap! values 
                        assoc :percent (-> % .-target .-value))}]
            [:button {:on-click #(.log js/console 
                        (c/clj->js @values))} "Add Address" ]]))

(defn main-view []
    [:div#main
        (for [[from splits] @all-splits]
            ^{:key from}
            [:div 
                [:h2 from]
                (splits-view splits)
                (insert-new)])])

(r/render-component [main-view] 
    (.getElementById js/document "mainDisplay"))