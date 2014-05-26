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

(defn add-address [address percent new-splits]
    (fn [ ]
       (print (str @address " " @percent))
       (let [values {:address @address :percent @percent}]
          (put! new-splits values)
          (reset! address "")
          (reset! percent "")
          )))

(defn update-value [val-atom]
    (fn [element]
        (reset! val-atom
           (-> element .-target .-value))))

(defn insert-new [new-splits needs-percent]
    (let [percent (r/atom nil)
          address (r/atom nil)]
      (fn [new-splits needs-percent]
          [:div
              [:input {:placeholder "Split to new address"
                       :type "text"
                       :value @address
                       :on-change (update-value address)}]
              (if needs-percent
                  [:input {:type "text"
                           :value @percent
                           :on-change (update-value percent)}])
              [:button 
                  {:on-click (add-address address percent new-splits)} 
              "Add Address"]])))

; The issue: the address and percent that get set w/ update-value don't seem
; to correspeond w/ the address and percent in add-address. Maybe should be wrapped in a function?
; address percent are nil in add-address when they clearly shouldn't be, they should be mutable
; refs, right?

(defn main-view [all-splits new-splits]
    [:div#main
        (for [[from splits] @all-splits
              subsplits [(splits-view splits)]]
            ^{:key from}
            [:div 
                [:h2 from]
                subsplits
                [insert-new 
                 (map> #(assoc % :from from) new-splits)
                 (-> subsplits empty? not)]])])

(r/render-component [main-view all-splits new-splits] 
    (.getElementById js/document "mainDisplay"))