(ns bitsplit.core
  (:use compojure.core)
  (:require [compojure.route :as route]))

(defroutes app
  (GET "/splits" [] "<h1>Hello World</h1>")
  (POST "/splits/:from/:to/:percentage" [] "yo")
  (DELETE "/splits/:from/:to" [] "hey")
  (route/not-found "<h1>Page not found</h1>"))