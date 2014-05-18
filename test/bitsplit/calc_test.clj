(ns bitsplit.calc-test
    (:use clojure.test.check.clojure-test
          midje.sweet)
    (:require
        [clojure.test.check :as tc]
        [clojure.test.check.generators :as gen]
        [clojure.test.check.properties :as prop] 
        [bitsplit.calculate :as calc]))

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

(def gen-decimal (->> gen/s-pos-int
                    (gen/fmap (comp str float #(/ % 100)))
                    (gen/fmap #(java.math.BigDecimal. %))))
(def gen-neg-dec (gen/fmap #(- %) gen-decimal))
(def gen-address (gen/fmap #(str "address" %) (gen/elements (-> 10 range vec))))
(def gen-mods (gen/tuple gen-address gen-address gen-decimal))

(def val-sum #(->> % (map last) (reduce + 0M)))

(defspec apply-difference-works
    100
    (prop/for-all
        [percentages (gen/map gen-address gen-decimal)
         diff (gen/one-of [gen-decimal gen-neg-dec])]
        (let [before percentages
              after (calc/apply-diff diff before)]
            (= (+ (val-sum before) diff)
               (val-sum after)))))


(defn one-or-zero? [number]
    (or 
        (= 0M number)
        (= 1M number)))

(defspec save-percentage-adjusts-things
    100
    (prop/for-all
        [modifications (gen/vector (gen/tuple gen-address gen-decimal))]
        (->> modifications
            (reduce #(apply calc/save-percentage %1 %2) { })
            val-sum
            one-or-zero?)))

; (defspec percentages-always-preserved
;          100 
;         (prop/for-all
;             [modifications (gen/vector gen-mods)]
;             (let [changed
;                     (reduce #(apply calc/save-split %1 %2) 
;                             { } modifications)]
;                 (->> changed
;                      (map last)
;                      (map calc/one?)
;                      (reduce #(and %1 %2) true)))))
