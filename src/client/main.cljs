(ns bitsplit.main
    (:require [reagent.core :as r]
              [cljs.reader :refer [read-string]]
              [cljs.core :as c]
              [cljs.core.async :refer [take! put! map> map< <! chan]]
              [fluyt.requests :as requests])
    (:use-macros [cljs.core.async.macros :only [go]]))

(def print #(.log js/console %))
(defn flip [function] #(function %2 %1))

(def all-splits (r/atom { }))
    
(->> (requests/get "http://localhost:3000/splits")
    (map< (comp read-string :body))
    ((flip take!)
        (fn [result]
            (print result)
            (reset! all-splits result))))

(defn add-address [from to percent]
    (->> (requests/post 
            (str "http://localhost:3000/splits/"
                  from "/" to "?percentage=" percent))
        (map< (comp read-string :body))
        ((flip take!)
            #(reset! all-splits %))))

(def new-splits (chan))
(go
    (while true
        (let [{from :from to :address percent :percent}
                 (<! new-splits)]
            ; (print from to percent)
            (add-address from to (or percent 1)))))

(defn splits-view [splits]
    (for [[to percentage] splits]
        ^{:key to}
        [:p 
            [:span to] ": "
            [:span percentage]]))

(defn insert-new [new-splits new?]
    (let [values (atom { })]
        [:div
            [:input {:placeholder "Split to new address"
                     :on-change #(swap! values 
                        assoc :address (-> % .-target .-value))}]
            (if (not new?)
                [:input {:on-change #(swap! values 
                        assoc :percent (-> % .-target .-value))}])
            [:button {:on-click #(put! new-splits @values)} "Add Address" ]]))

(defn main-view [all-splits new-splits]
    [:div#main
        (for [[from splits] @all-splits
              subsplits [(splits-view splits)]
              new? [(empty? subsplits)]]
            ^{:key from}
            [:div 
                [:h2 from]
                subsplits
                (insert-new 
                 (map> #(assoc % :from from) new-splits)
                 new?)])])

(r/render-component [main-view all-splits new-splits] 
    (.getElementById js/document "mainDisplay"))