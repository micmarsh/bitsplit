(ns bitsplit.core
  (:use bitsplit.clients.bitcoind
        bitsplit.storage.filesystem
        bitsplit.clients.protocol)
  (:require [bitsplit.handlers :as handlers]
            [bitsplit.transfer :as transfer]
            [bitsplit.views.main :as ui]))

(defn mapmap [fn seq & others]
    (into { } (apply map fn seq others)))

(defn make-storage [client]
    (-> {:data (mapmap (fn [addr] [addr { }]) (addresses client)) ; less neccesary once this is reading from persistence
         :location SPLITS_LOCATION
         :persist? false}
        map->BalancedFile))

(defn -main [ & [mode] ]
        (let [client (->Bitcoind "")
              storage (atom (make-storage client))
              changes (clojure.core.async/chan)
              unspents (unspent-channel client)]
            (if (= mode "headless")
                (println "Starting Bitsplit Process...")
                (let [actions (ui/start-ui! (handlers/list-all storage) changes)]
                  (println "Starting Bitsplit UI...")
                  (handlers/handle-actions! storage actions changes)))
            (transfer/handle-unspents! client storage unspents))) 