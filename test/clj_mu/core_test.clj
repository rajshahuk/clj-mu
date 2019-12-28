(ns clj-mu.core-test
  (:require [clojure.test :refer :all]
            [clj-mu.core :refer :all]
            [clojure.tools.logging :as log]
            [clj-http.client :as client])
  (:import (io.muserver MuServer)
           (java.net ServerSocket)))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))

(deftest create-a-mu-server-instance-on-random-port
  (testing "no parameters are passed in and mu should start on a random port"
    (let [mu (run-mu)
          mu-uri (.uri mu)
          _ (log/info "mu started here: " (.toString mu-uri))
          response (client/get (.toString mu-uri) {:throw-exceptions false})
          _ (.stop mu)]
      (is (not (nil? response)))
      (is (= 404 (:status response))))))

(defn- find-a-free-port
  "find a free port"
  []
  (let [server-socket (ServerSocket. 0)
        port (.getLocalPort server-socket)
        _ (.close server-socket)]
    port))

(deftest create-a-mu-server-instance-on-port-of-our-choice
  (testing "if we can create a mu instance"
    (let [port (find-a-free-port)
          _ (log/info "free port:" port)
          mu (run-mu {:port port})
          mu-uri (.uri mu)
          _ (.stop mu)]
      (is (= port (.getPort mu-uri)))
      )))