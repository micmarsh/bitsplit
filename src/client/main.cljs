(ns bitsplit.main
    (:require [reagent.core :as r]
              [cljs.reader :refer [read-string]]
              [cljs.core :as c]
              [cljs.core.async :refer [take! map<]]
              [fluyt.requests :as requests]))

(def print #(.log js/console %))
(defn flip [function] #(function %2 %1))

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

(defn insert-new [new-channels new?]
    (let [values (atom { })]
        [:div
            [:input {:placeholder "Split to new address"
                     :on-change #(swap! values 
                        assoc :address (-> % .-target .-value))}]
            (if (not new?)
                [:input {:on-change #(swap! values 
                        assoc :percent (-> % .-target .-value))}])
            [:button {:on-click #(.log js/console 
                        (c/clj->js @values))} "Add Address" ]]))

(defn main-view [all-splits]
    [:div#main
        (for [[from splits] @all-splits
              subsplits [(splits-view splits)]
              new? [(empty? subsplits)]]
            ^{:key from}
            [:div 
                [:h2 from]
                subsplits
                (insert-new nil new?)])])

(r/render-component [main-view all-splits] 
    (.getElementById js/document "mainDisplay"))