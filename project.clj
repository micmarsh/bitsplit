(defproject bitsplit "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/test.check "0.5.8"]
                 [org.clojure/clojure "1.5.1"]
                 [compojure "1.1.8"]
                 [midje "1.6.3"]

                 ;[clj-btc "0.1.1"]
                 [org.clojure/data.json "0.2.3"]
                 [http-kit "2.1.11"]]

  :plugins [[lein-ring "0.7.1"]
            [lein-midje "3.0.0"]]
  :ring {:handler bitsplit.core/app})
