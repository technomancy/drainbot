(defproject drainbot "0.1.0-SNAPSHOT"
  :description "HTTPS -> IRC drains"
  :url "https://drainbot.herokuapp.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.2.1"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [irclj "0.5.0-alpha4"]]
  :uberjar-name "drainbot.jar"
  :profiles {:uberjar {:aot :all}}
  :min-lein-version "2.5.0")
