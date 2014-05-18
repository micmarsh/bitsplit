(ns bitsplit.core
  (:use compojure.core)
  (:require [compojure.route :as route]
            [bitsplit.handlers :as handlers]))

(defroutes app
    (GET "/splits" [] 
        (fn [request] (str "YO" request)))
    (POST "/splits/:from/:to/:percentage" [] 
        (handlers/save! from to percentage))
    (DELETE "/splits/:from/:to" [] 
        (handlers/delete! from to))
    (route/not-found "<h1>Page not found</h1>"))