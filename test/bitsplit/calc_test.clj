(ns bitsplit.calc-test
    (:use midje.sweet)
    (:require [bitsplit.calculate :as calc]))

(fact "base case returns empty"
    (calc/apply-diff 0.2M { }) => { })