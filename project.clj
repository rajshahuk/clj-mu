(defproject com.twelvenines/clj-mu "0.1.2"
  :description "Clojure friendly library for using https://muserver.io"
  :url "https://github.com/rajshahuk/clj-mu"
  :license {:name "MIT License"
            :url "https://github.com/rajshahuk/clj-mu/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.11.3"]
                 [io.muserver/mu-server "1.1.2"]
                 [org.clojure/tools.logging "1.3.0"]]
  :profiles {:dev {:resource-paths ["test/resources"]
                    :dependencies   [[cheshire "5.13.0"]
                                     [clj-http "3.13.0"]
                                     [org.slf4j/slf4j-api "2.0.13"]
                                     [ch.qos.logback/logback-classic "1.5.6"]]}}
  :repl-options {:init-ns clj-mu.core})
