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
       (let [values {:address @address :percent @percent}]
          (put! new-splits values)
          (reset! address "")
          (reset! percent ""))))

(defn update-value [val-atom]
    (fn [element]
        (reset! val-atom
           (-> element .-target .-value))))

(defn on-key [keycode callback]
    (fn [element]
        (let [which (.-which element)]
            (when (= keycode which)
                (callback)))))

(defn insert-new [new-splits needs-percent]
    (let [percent (r/atom "")
          address (r/atom "")
          save (add-address address percent new-splits)
          on-enter (on-key 13 save)]
      (fn [new-splits needs-percent]
          [:div
              [:input {:placeholder "Split to new address"
                       :type "text"
                       :value @address
                       :on-change (update-value address)
                       :on-key-up on-enter}]
              (if needs-percent
                  [:input {:type "text"
                           :value @percent
                           :on-change (update-value percent)
                           :on-key-up on-enter}])
              [:button {:on-click save} "Add Address"]])))

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

(r/render-component
    [main-view all-splits new-splits] 
    (.getElementById js/document "mainDisplay"))