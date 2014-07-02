(ns bitsplit.core
  (:use  bitsplit.storage.filesystem
        bitsplit.clients.protocol)
  (:require [bitsplit.handlers :as handlers]
            [bitsplit.transfer :as transfer]))


(defn -main [ & [mode] ]
    (println "sup")) 