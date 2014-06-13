(ns bitsplit.core
  (:use compojure.core
        [ring.middleware.params :only (wrap-params)]
        [ring.middleware.resource :only (wrap-resource)]
        [ring.adapter.jetty :only (run-jetty)]
         [ring.util.response :only (redirect)])
  (:require [compojure.route :as route]
            [bitsplit.handlers :as handlers]
            [clojure.data.json :as json]

            [bitsplit.transfer :as transfer]
            [bitsplit.bitcoind :as rpc])
  (:gen-class))

(defroutes app-routes
    (GET "/" [] (clojure.java.io/resource "client/index.html"))
    (GET "/splits" [] handlers/list-all)
    (POST "/splits/:from/:to" [] handlers/save!)
    (DELETE "/splits/:from/:to" [] handlers/delete!)
    (route/not-found "<h1>Page not found</h1>"))

; really should use liberator
(def app (-> app-routes
            (wrap-resource "client")
            wrap-params))

(defmacro thread-loop [& body]
    `(.start (Thread. 
        (fn [] 
            (while true
                ~@body)))))

(defn thread-sleep [minutes]
    (Thread/sleep (* minutes 1000 60)))

(def INTERVAL 0.1);(/ 1 30))

(defn -main [& [port]]
    (try
        ; (thread-loop
        ;     (thread-sleep INTERVAL)
        ;     (let [percentages (-> nil handlers/list-all read-string)
        ;           unspent (rpc/list-unspent)]
        ;         (transfer/make-transfers! percentages unspent)))
        (run-jetty app {:port (if port (Integer. port) 3026)})
    (catch java.net.ConnectException e 
        (println "You need a running bitcoind instance!"))))