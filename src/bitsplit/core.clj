(ns bitsplit.core
  (:use compojure.core
        [ring.middleware.params :only (wrap-params)]
        [ring.adapter.jetty :only (run-jetty)])
  (:require [compojure.route :as route]
            [bitsplit.handlers :as handlers]
            [clojure.data.json :as json]

            [bitsplit.transfer :as transfer]
            [bitsplit.bitcoind :as rpc])
  (:gen-class))

(defroutes app-routes
    (GET "/splits" [] handlers/list-all)
    (POST "/splits/:from/:to" [] handlers/save!)
    (DELETE "/splits/:from/:to" [] handlers/delete!)
    (route/files "/" 
        {:root 
            (str (System/getProperty "user.dir") 
                "/resources/client")})
    (route/not-found "<h1>Page not found</h1>"))

; really should use liberator
(def app (-> app-routes
            wrap-params))

(defmacro thread-loop [& body]
    `(.start (Thread. 
        (fn [] 
            (loop []
                ~@body
                (recur))))))

(defn -main []
    (try
        (thread-loop
            (Thread/sleep (* 5000 1))
            (let [percentages (-> nil handlers/list-all read-string)
                  unspent (rpc/list-unspent)]
                (println percentages)
                (println unspent)
                (transfer/make-transfers! percentages unspent)))
        (run-jetty app {:port 3026})
    (catch java.net.ConnectException e 
        (println "You need a running bitcoind instance!"))))