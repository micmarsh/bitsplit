(ns bitsplit.ui
    (:require [reagent.core :as r]
              [cljs.core.async :refer [put! map> chan <!]]
              [bitsplit.calculate :as calc])
    (:use [bitsplit.state :only [all-splits new-splits]])
    (:use-macros [marshmacros.coffee :only [cofmap]]
                 [cljs.core.async.macros :only [go]]))

(def print #(.log js/console %))

(defn splits-view [splits]
    (for [[to percentage] splits]
        ^{:key to}
        [:p 
            [:span to] ": "
            [:span percentage]]))

(defn add-address [{:keys [errors address percent new-splits]}]
    (fn [ ]
      (let [percentage (or (js/Number @percent) 0)]
        (cond 
          (->> @address (.address js/validate) not)
              (put! errors :address)
          (or (> percentage 1) (>= 0 percentage))
              (put! errors :percent)
          :else
            (do
              (put! new-splits 
                  {:address @address :percent percentage})
              (reset! address "")
              (reset! percent ""))))))

(defn update-value [val-atom]
    (fn [element]
        (reset! val-atom
           (-> element .-target .-value))))

(defn on-key [keycode callback]
    (fn [element]
        (let [which (.-which element)]
            (when (= keycode which)
                (callback)))))

(defn set-errors [{:keys [errors error-message]}]
    (go 
        (while true
            (let [error (<! errors)]
              (do
                (reset! error-message 
                    (cond (= error :address)
                            "Not a Bitcoin Address"
                          (= error :percent)
                            "Not a Percentage"
                          :else ""))
                (js/setTimeout
                    #(reset! error-message "")
                  2000))))))

(defn insert-new [new-splits needs-percent]
    (let [error-message (r/atom "")
          percent (r/atom "")
          address (r/atom "")
          errors (chan)
          save (add-address (cofmap address percent new-splits errors))
          on-enter (on-key 13 save)]
      (set-errors (cofmap errors error-message))
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
              [:button {:on-click save} "Add Address"]
              [:br]
              [:p {:style {:color "red"}} @error-message]])))

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