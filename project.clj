(defproject com.twelvenines/clj-mu "0.0.23"
  :description "Clojure friendly library for using https://muserver.io"
  :url "https://github.com/rajshahuk/clj-mu"
  :license {:name "MIT License"
            :url "https://github.com/rajshahuk/clj-mu/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [io.muserver/mu-server "0.72.7"]
                 [org.clojure/tools.logging "1.2.4"]
                 [ch.qos.logback/logback-classic "1.2.11"]]
  :profiles {:test {:resource-paths ["test/resources"]
                    :dependencies   [[cheshire "5.10.2"]
                                     [clj-http "3.12.3"]]}}
  :repl-options {:init-ns clj-mu.core})
