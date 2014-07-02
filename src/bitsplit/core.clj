(ns bitsplit.core
  (:use bitsplit.storage.protocol
        bitsplit.clients.protocol
        [clojure.core.async :only (go put! <!)])
  (:require [bitsplit.transfer :as transfer]
            [bitsplit.utils.calculate :as calc]))

(defn modify-address! [modifier storage {:keys [parent address percent]}]
    (let [existing (lookup storage parent)
            adjusted (if percent 
                        (modifier existing address percent)
                        (modifier existing address))]
          (save! storage parent adjusted)))

(def add-address! (partial modify-address! calc/save-percentage))
    
(def remove-address! (partial modify-address! calc/delete-percentage))

(defn new-split! [storage client] 
    (let [address (new-address! client)]
        (save! storage address { })
        address))

(defn remove-split! [storage {:keys [split]}]
    (delete! storage split)
    split)

(def edit-address! add-address!) ; maybe in the future each of these
; can throw exceptions if they're not actually called the right way

(def list-all all)

(defn -main [ & [mode] ]
    (println "sup")) 