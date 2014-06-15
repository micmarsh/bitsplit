(ns bitsplit.core
  (:use compojure.core
        bitsplit.clients.bitcoind
        bitsplit.clients.protocol)
  (:require [bitsplit.handlers :as handlers]
            [bitsplit.transfer :as transfer]))

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
    (println "sup")
    (catch java.net.ConnectException e 
        (println "You need a running bitcoind instance!"))))