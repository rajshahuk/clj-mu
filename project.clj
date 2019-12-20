(defproject clj-mu "0.1.0-SNAPSHOT"
  :description "Clojure friendly library for using https://muserver.io"
  :url "https://github.com/rajshahuk/clj-mu"
  :license {:name "MIT License"
            :url "https://github.com/rajshahuk/clj-mu/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [io.muserver/mu-server "0.47.3"]]
  :repl-options {:init-ns clj-mu.core})
