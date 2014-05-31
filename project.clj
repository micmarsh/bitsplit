(defproject bitsplit "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [org.clojure/test.check "0.5.8"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/clojure "1.5.1"]
                 [fluyt "0.1.0-SNAPSHOT"]
                 [marshmacros "0.2.1"]
                 [compojure "1.1.8"]
                 [reagent "0.4.2"]
                 [digest "1.4.4"]
                 [midje "1.6.3"]
                 [ring "1.2.2"]

                 ;[clj-btc "0.1.1"]
                 [org.clojure/data.json "0.2.3"]
                 [http-kit "2.1.11"]]

  :plugins [[lein-ring "0.7.1"]
            [lein-midje "3.0.0"]
            [lein-cljsbuild "1.0.2"]
            [com.keminglabs/cljx "0.3.2"]]

  :main bitsplit.core

   ;; uberjar
  :uberjar-name "bitsplit.jar"
  :aot :all
  :omit-source true

  :source-paths ["src/" "target/generated-src/clj/"]

  :cljx {:builds [{:source-paths ["src/"]
                 :output-path "target/generated-src/clj"
                 :rules :clj}

                {:source-paths ["src/"]
                 :output-path "target/generated-src/cljs"
                 :rules :cljs}]}
                 
  :hooks [cljx.hooks]

  :ring {:handler bitsplit.core/app}
  :cljsbuild
      {:builds [{:source-paths ["src/client/" "target/generated-src/cljs/"]
                   :compiler
                     {:preamble ["reagent/react.js"]
                      :output-to "resources/client/main.js"
                      :pretty-print true}}]})
