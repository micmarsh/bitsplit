(ns bitsplit.core
  (:use compojure.core
        [ring.middleware.params :only (wrap-params)]
        [ring.adapter.jetty :only (run-jetty)])
  (:require [compojure.route :as route]
            [bitsplit.handlers :as handlers]
            [clojure.data.json :as json])
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

(defn -main []
    (try
        (run-jetty app {:port 3026})
    (catch java.net.ConnectException e 
        (println "You need a running bitcoind instance!"))))