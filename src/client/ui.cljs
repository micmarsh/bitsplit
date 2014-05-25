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


(defn add-address [val-atom new-splits]
    (fn []
        (put! new-splits @val-atom)
        ; TODO still need to actually clear dom elements
        ))

(defn update-values [key val-atom]
    (fn [element]
        (swap! val-atom
          assoc key (-> element .-target .-value))))


(defn insert-new [new-splits needs-percent]
    (let [values (atom { })]
        [:div
            [:input {:placeholder "Split to new address"
                     :on-change (update-values :address values)}]
            (if needs-percent
                [:input {:on-change (update-values :percent values)}])
            [:button {:on-click (add-address values new-splits)} "Add Address"]]))

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