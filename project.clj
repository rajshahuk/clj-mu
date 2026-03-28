(defproject com.twelvenines/clj-mu "1.0.1"
  :description "Clojure friendly library for using https://muserver.io"
  :url "https://github.com/rajshahuk/clj-mu"
  :license {:name "MIT License"
            :url "https://github.com/rajshahuk/clj-mu/blob/master/LICENSE"}
  :dependencies [[org.clojure/tools.logging "1.3.1"]]
  :profiles {:dev {:resource-paths ["test/resources"]
                    :dependencies   [[org.clojure/clojure "1.12.4"]
                                     [io.muserver/mu-server "2.2.6"]
                                     [cheshire "6.2.0"]
                                     [clj-http "3.13.1"]
                                     [org.slf4j/slf4j-api "2.0.17"]
                                     [ch.qos.logback/logback-classic "1.5.32"]]}}
  :repl-options {:init-ns clj-mu.core})
