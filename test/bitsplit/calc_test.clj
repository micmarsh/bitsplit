(ns bitsplit.calc-test
    (:use clojure.test.check.clojure-test
          midje.sweet)
    (:require
        [clojure.test.check :as tc]
        [clojure.test.check.generators :as gen]
        [clojure.test.check.properties :as prop] 
        [bitsplit.utils.calculate :as calc]))

(def gen-decimal (->> gen/s-pos-int
                    (gen/fmap (comp #(/ % 100) #(mod % 100)))
                    (gen/fmap (comp str float))
                    (gen/fmap #(java.math.BigDecimal. %))))
(def gen-neg-dec (gen/fmap #(- %) gen-decimal))
(def gen-address (gen/fmap #(str "address" %) (gen/elements (-> 10 range vec))))
(def gen-mods (gen/tuple gen-address gen-address gen-decimal))
(def gen-deletes (gen/tuple gen-address gen-address))

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
              after (calc/apply-diff diff before)]
            (big= (+ (val-sum before) diff)
                  (val-sum after)))))

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

(defn save-or-delete [data args]
      (let [save? #(= (count %) 3)
            method (if (save? args)
                      calc/save-split
                      calc/delete-split)]
            (apply method data args)))

(defspec percentages-always-preserved
    100 
    (prop/for-all
        [modifications (gen/vector (gen/one-of [gen-mods gen-deletes]))]
        (let [changed
                (reduce save-or-delete { } modifications)]
            (->> changed
                 (map (comp one-or-zero? val-sum last))
                 (reduce #(and %1 %2) true)))))

(def single-percent {"foobar" { "other" 1M } })
(def sample-txs 
    {"foobar" 2M
     "woooo"  1M})

(fact "build totals works"
    (calc/build-totals single-percent sample-txs)
        => {"other" 2M}
    (calc/build-totals single-percent { }) => empty?
    (calc/build-totals { } sample-txs) => empty?)