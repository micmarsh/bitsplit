(ns bitsplit.core
  (:use compojure.core
        [ring.middleware.params :only (wrap-params)])
  (:require [compojure.route :as route]
            [bitsplit.handlers :as handlers]))

(defroutes app-routes
    (GET "/splits" [] 
        (fn [request] (str "YO" request)))
    (POST "/splits/:from/:to" [] handlers/save!)
    (DELETE "/splits/:from/:to" [] handlers/delete!)
    (route/not-found "<h1>Page not found</h1>"))


(def app (-> app-routes
            wrap-params))