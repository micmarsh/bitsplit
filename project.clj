(defproject bitsplit-core "0.1.1"
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
        :plugins [[lein-midje "3.0.0"]]
      }
  })
