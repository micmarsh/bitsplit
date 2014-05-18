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