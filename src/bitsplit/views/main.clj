(ns bitsplit.views.main
    (:use seesaw.core
        [bitsplit.mock :only (sample-data)])
    (:require [clojure.core.async :refer [<!] :as async]))

(def map-list 
    (comp 
        (partial vertical-panel :items) 
        map))

(defn percentage->ui [[address percentage]]
    (left-right-split
        (label address)
        (label percentage)))

(def compact (partial filter identity))
(defn register-button [actions address percentage parent button]
    (listen button :action 
        (fn [e]
            (async/put! actions
                {:type :add-address
                 :from parent
                 :to (value address)
                 :percentage (value percentage)})))
    button)

(defn address-adder [actions parent percentage?]
    (let [address (text "")
          percentage (text (if percentage? "" "1"))]
        (flow-panel :items (compact [ 
            address
            (when percentage? percentage)
            (->> (button :text "Add Address" )
                (register-button actions address percentage parent))]))))

(defn entry->ui [actions [address percentages]]
    (vertical-panel
       :id (keyword address)
       :items [
        (label address)
        (map-list percentage->ui percentages)
        (address-adder actions address
            (-> percentages empty? not))]))

(defn splits->ui [actions splits]
    (->> splits
        (map-list (partial entry->ui actions))
        scrollable))

(defn second-item [items thing]
    (let [head (first items)
          tail (-> items rest rest)]
        (->> tail
            (cons thing)
            (cons head))))

(defn new-addresses [panel percentages]
    (let [panel-items (config panel :items)
          new-items (second-item panel-items (map-list percentage->ui percentages))]
        (config! panel :items new-items)))

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

