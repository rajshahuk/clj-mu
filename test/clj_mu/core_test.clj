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

(deftest create-http-mu-server-instance-on-port-of-our-choice
  (testing "mu starting with http on a port of our choice"
    (let [port (find-a-free-port)
          _ (log/info "free port:" port)
          mu (run-mu {:port port})
          mu-uri (.uri mu)
          _ (.stop mu)]
      (is (= port (.getPort mu-uri)))
      )))

(deftest create-https-mu-server-instance-on-port-of-our-choice
  (testing "mu starting with https on a port of our choice"
    (let [port (find-a-free-port)
          _ (log/info "free port:" port)
          mu (run-mu {:https-port port})
          mu-uri (.uri mu)
          _ (log/info "mu started here: " (.toString mu-uri))
          response (client/get (.toString mu-uri) {:insecure? true
                                                   :throw-exceptions false})
          _ (.stop mu)]
      (is (= port (.getPort mu-uri)))
      (is (not (nil? response)))
      (is (= 404 (:status response)))
      ))
  (testing "mu starting with http and a https on a port of our choice"
    (let [port-one (find-a-free-port)
          port-two (find-a-free-port)
          _ (log/info "free-one:" port-one "free-two:" port-two)
          mu (run-mu {:port port-one :https-port port-two})
          mu-uri-http  (.httpUri mu)
          mu-uri-https (.httpsUri mu)
          _ (log/info "mu started here:" (.toString mu-uri-http) "and here:" (.toString mu-uri-https))
          response-http (client/get (.toString mu-uri-http) {:throw-exceptions false})
          response-https (client/get (.toString mu-uri-http) {:insecure? true :throw-exceptions false})
          _ (.stop mu)]
      (is (= port-one (.getPort mu-uri-http)))
      (is (= port-two (.getPort mu-uri-https)))
      (is (not (nil? response-http)))
      (is (= 404 (:status response-http)))
      (is (not (nil? response-https)))
      (is (= 404 (:status response-https)))
      )))

(deftest hello-world
  (testing "a very basic functional GET request"
    (let [mu-builder (configure-mu)
          mu-server (-> mu-builder
                        (GET "/" (fn [request] {:status 200 :body "Hello, World!"}))
                        (start-mu))
          mu-uri (.uri mu-server)
          response (client/get (.toString mu-uri))
          _ (log/info "response:" response)]
      (is (= 200 (:status response)))
      (is (= "Hello, World!" (:body response)))
      )
    ))