(defproject poe "0.1.0-SNAPSHOT"
  :description "a small poetry organizer"
  :url "http://example.com/FIXME"
  :license {:name "GNU GPLv3"
            :url "http://www.gnu.org"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/java.jdbc "0.3.0-alpha4"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [seesaw "1.4.0"]]
  :main poe.core
  :profiles {:uberjar {:aot :all}})
