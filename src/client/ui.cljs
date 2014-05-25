(ns bitsplit.ui
    (:require [reagent.core :as r]
              [cljs.core.async :refer [put! map>]]
              [bitsplit.calculate :as calc])

    (:use [bitsplit.state :only [all-splits new-splits]]))

(def print #(.log js/console %))

(defn splits-view [splits]
    (for [[to percentage] splits]
        ^{:key to}
        [:p 
            [:span to] ": "
            [:span percentage]]))

(defn insert-new [new-splits needs-percent]
    (let [values (atom { })]
        [:div
            [:input {:placeholder "Split to new address"
                     :on-change #(swap! values 
                        assoc :address (-> % .-target .-value))}]
            (if needs-percent
                [:input {:on-change #(swap! values 
                        assoc :percent (-> % .-target .-value))}])
            [:button {:on-click #(put! new-splits @values)} "Add Address"]]))

(defn main-view [all-splits new-splits]
    [:div#main
        (for [[from splits] @all-splits
              subsplits [(splits-view splits)]]
            ^{:key from}
            [:div 
                [:h2 from]
                subsplits
                (insert-new 
                 (map> #(assoc % :from from) new-splits)
                 (-> subsplits empty? not))])])

(r/render-component [main-view all-splits new-splits] 
    (.getElementById js/document "mainDisplay"))