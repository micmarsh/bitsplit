(ns bitsplit.core
  (:use compojure.core
        bitsplit.clients.bitcoinj
        bitsplit.storage.filesystem
        bitsplit.clients.protocol)
  (:require [bitsplit.handlers :as handlers]
            [bitsplit.transfer :as transfer]
            [bitsplit.views.main :as ui]))

(defn mapmap [fn seq & others]
    (into { } (apply map fn seq others)))

(defn make-storage [client]
    (-> {:data (mapmap (fn [addr] [addr { }]) (addresses client))
         :location SPLITS_LOCATION
         :persist? false}
        map->BalancedFile))

(def client (->Bitcoinj (new-wallet)))

(defn -main [ ]
    (try
        (let [storage (atom (make-storage client))
              changes (clojure.core.async/chan)
              actions (ui/start-ui! (handlers/list-all storage) changes)
              unspents (unspent-channel client)]
              (transfer/handle-unspents! client storage unspents)
              (handlers/handle-actions! storage actions changes))
    (catch java.net.ConnectException e 
        (println "You need a running bitcoind instance!"))))