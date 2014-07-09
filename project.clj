(defproject bitsplit-core "0.1.2"
  :description "Provides the core functions and protocols necessary for a bitsplit implementation"
  :url "http://github.com/micmarsh/bitsplit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [org.clojure/clojure "1.5.1"]]

  :profiles {
    :dev {
        :dependencies [[org.clojure/test.check "0.5.8"]
                       [midje "1.6.3"]]
        :plugins [[com.keminglabs/cljx "0.4.0"]
                  [lein-cljsbuild "1.0.3"]
                  [lein-midje "3.0.0"]
                  [lein-npm "0.4.0"]]
        :hooks [cljx.hooks]
        :cljsbuild {
          :builds [{
              :source-paths ["target/cljs"]
              :compiler {
                :output-to "target/main.js"
                :optimizations :whitespace
                :pretty-print true}}
        ]}
        :cljx {
          :builds [
                {:source-paths ["src/bitsplit"]
                 :output-path "target/classes"
                 :rules :clj}
                {:source-paths ["src/bitsplit"]
                 :output-path "target/classes"
                 :rules :cljs}
        ]}
    }
})
