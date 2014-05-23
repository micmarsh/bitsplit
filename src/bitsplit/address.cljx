(ns bitsplit.address
    #+cljs (:use [cljs.core :only [clj->js]]))

(def digits58 
    (#+clj vec
     #+cljs identity
    "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"))

(defn sha256 [bytes] "lol")

(defn decode-char [acc char]
    (let [index (.indexOf digits58 char)]
        (-> acc (* 58) (+ index))))

(defn decode-base58 [address, length]
    (let []))
