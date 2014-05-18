(ns bitsplit.calc-test
    (:use midje.sweet)
    (:require [bitsplit.calculate :as calc]))

(fact "base case returns empty"
    (calc/apply-diff 0.2M { }) => { })

(def mono-base {"hello" 0.07M})

(fact "add and subtract to single case"
    (calc/apply-diff 0.3M mono-base) 
        => {"hello" 0.37M}
    (calc/apply-diff -0.04M mono-base)
        => {"hello" 0.03M})

(def double-base {"hello" 0.4M "yo" 1.2M})
(def triple-base (conj double-base ["heyyo" 0.5M]))

(fact "can handle actually dividing things"
    (calc/apply-diff 0.6M double-base)
        => {"hello" 0.7M "yo" 1.5M}
    (calc/apply-diff 0.93M triple-base)
        => {
            "hello" 0.71M "yo" 1.51M
            "heyyo" 0.81M
            })