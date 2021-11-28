(defproject com.twelvenines/clj-mu "0.0.22"
  :description "Clojure friendly library for using https://muserver.io"
  :url "https://github.com/rajshahuk/clj-mu"
  :license {:name "MIT License"
            :url "https://github.com/rajshahuk/clj-mu/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [io.muserver/mu-server "0.70.0"]
                 [org.clojure/tools.logging "1.1.0"]
                 [ch.qos.logback/logback-classic "1.2.7"]]
  :profiles {:test {:resource-paths ["test/resources"]
                    :dependencies   [[cheshire "5.10.1"]
                                     [clj-http "3.12.3"]]}}
  :repl-options {:init-ns clj-mu.core})
