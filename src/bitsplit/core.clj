(ns bitsplit.core
  (:use compojure.core
        bitsplit.clients.bitcoind
        bitsplit.storage.filesystem
        bitsplit.clients.protocol)
  (:require [bitsplit.handlers :as handlers]
            [bitsplit.transfer :as transfer]
            [bitsplit.views.main :as ui]))

(defn mapmap [fn seq & others]
    (into { } (apply map fn seq others)))

(defn make-storage [ ]
    (-> {:data (mapmap (fn [addr] [addr { }]) ["trololololo", "hello"]);;(rpc/list-addresses))
         :location SPLITS_LOCATION
         :persist? false}
        map->BalancedFile))

(def client (->Bitcoind ""))

(defmacro thread-loop [& body]
    `(.start (Thread. 
        (fn [] 
            (while true
                ~@body)))))

(defn thread-sleep [minutes]
    (Thread/sleep (* minutes 1000 60)))

(def INTERVAL 0.1);(/ 1 30))

(defn -main [ ]
    (try
        ; (thread-loop
        ;     (thread-sleep INTERVAL)
        ;     (let [percentages (-> nil handlers/list-all read-string)
        ;           unspent (unspent-amounts client)]
        ;         (transfer/make-transfers! client percentages unspent)))
        ; (run-jetty app {:port (if port (Integer. port) 3026)})
        (let [storage (atom (make-storage))
              changes (clojure.core.async/chan)
              actions (ui/start-ui! (handlers/list-all storage) changes)]
              (handlers/handle-actions! storage actions changes))
    (catch java.net.ConnectException e 
        (println "You need a running bitcoind instance!"))))