(defproject com.twelvenines/clj-mu "0.0.18"
  :description "Clojure friendly library for using https://muserver.io"
  :url "https://github.com/rajshahuk/clj-mu"
  :license {:name "MIT License"
            :url "https://github.com/rajshahuk/clj-mu/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.10.2"]
                 [io.muserver/mu-server "0.56.5"]
                 [org.clojure/tools.logging "1.1.0"]
                 [ch.qos.logback/logback-classic "1.2.3"]]
  :profiles {:test {:resource-paths ["test/resources"]
                    :dependencies   [[cheshire "5.10.0"]
                                     [clj-http "3.11.0"]]}}
  :repl-options {:init-ns clj-mu.core})

