(defproject integrator "0.1.0-SNAPSHOT"
    :description "FIXME: write description"
    :url "http://example.com/FIXME"
    :license {:name "Eclipse Public License"
              :url  "http://www.eclipse.org/legal/epl-v10.html"}
    :dependencies [[org.clojure/clojure "1.8.0"]
                   [org.clojure/tools.nrepl "0.2.10"]
                   [proto-repl "0.3.1"]
                   [org.clojure/data.json "0.2.6"]
                   [org.clojure/tools.logging "0.4.0"]
                   [log4j/log4j "1.2.17"]]
    :main ^:skip-aot integrator.core
    :target-path "target/%s"
    :profiles {:uberjar {:aot :all}})
