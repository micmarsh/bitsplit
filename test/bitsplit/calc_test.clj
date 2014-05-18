(ns bitsplit.calc-test
    (:use clojure.test.check.clojure-test
          midje.sweet)
    (:require
        [clojure.test.check :as tc]
        [clojure.test.check.generators :as gen]
        [clojure.test.check.properties :as prop] 
        [bitsplit.calculate :as calc]))



(def gen-decimal (->> gen/s-pos-int
                    (gen/fmap (comp str float #(/ % 100)))
                    (gen/fmap #(java.math.BigDecimal. %))))
(def gen-neg-dec (gen/fmap #(- %) gen-decimal))
(def gen-address (gen/fmap #(str "address" %) (gen/elements (-> 10 range vec))))
(def gen-mods (gen/tuple gen-address gen-address gen-decimal))

(def val-sum #(->> % (map last) (reduce + 0M) (with-precision 10)))

(defn big= [first second]
    (let [diff (- first second)]
        (< (Math/abs (float diff))
            0.00000001)))

(defspec apply-difference-works
    100
    (prop/for-all
        [percentages (gen/not-empty (gen/map gen-address gen-decimal))
         diff (gen/one-of [gen-decimal gen-neg-dec])]
        (let [before percentages
              after (calc/apply-diff diff before)
              b (+ (val-sum before) diff)
              a (val-sum after)]
            (when (not (big= a b))
                (println a b))
            (big= a b))))

(defn one-or-zero? [number]
    (or 
        (big= 0M number)
        (big= 1M number)))

(defspec save-percentage-adjusts-things
    100
    (prop/for-all
        [modifications (gen/vector (gen/tuple gen-address gen-decimal))]
        (->> modifications
            (reduce #(apply calc/save-percentage %1 %2) { })
            val-sum
            one-or-zero?)))
