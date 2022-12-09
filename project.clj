(defproject com.twelvenines/clj-mu "0.0.30"
  :description "Clojure friendly library for using https://muserver.io"
  :url "https://github.com/rajshahuk/clj-mu"
  :license {:name "MIT License"
            :url "https://github.com/rajshahuk/clj-mu/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [io.muserver/mu-server "0.72.19"]
                 [org.clojure/tools.logging "1.2.4"]
                 [ch.qos.logback/logback-classic "1.4.5"]
                 [org.slf4j/slf4j-api "2.0.5"]]
  :profiles {:test {:resource-paths ["test/resources"]
                    :dependencies   [[cheshire "5.11.0"]
                                     [clj-http "3.12.3"]]}}
  :repl-options {:init-ns clj-mu.core})
