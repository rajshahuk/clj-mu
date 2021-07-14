(ns clj-mu.core-test
  (:require [clojure.test :refer :all]
            [clj-mu.core :refer :all]
            [clojure.tools.logging :as log]
            [clj-http.client :as client])
  (:import (java.net ServerSocket)))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))

(deftest create-a-mu-server-instance-on-random-port
  (testing "no parameters are passed in and mu should start on a random port"
    (let [mu (run-mu)
          mu-uri (.uri mu)
          _ (log/info "mu started here: " (.toString mu-uri))
          response (client/get (.toString mu-uri) {:throw-exceptions false})
          status (stop-mu mu)]
      (is (not (nil? response)))
      (is (= true status))
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
          _ (stop-mu mu)]
      (is (= port (.getPort mu-uri)))
      )))

(deftest create-https-mu-server-instance-on-port-of-our-choice
  (testing "mu starting with https on a port of our choice"
    (let [port (find-a-free-port)
          _ (log/info "free port:" port)
          mu (run-mu {:https-port port})
          mu-uri (.uri mu)
          _ (log/info "mu started here: " (.toString mu-uri))
          response (client/get (.toString mu-uri) {:insecure?        true
                                                   :throw-exceptions false})
          _ (stop-mu mu)]
      (is (= port (.getPort mu-uri)))
      (is (not (nil? response)))
      (is (= 404 (:status response)))
      ))
  (testing "mu starting with http and a https on a port of our choice"
    (let [port-one (find-a-free-port)
          port-two (find-a-free-port)
          _ (log/info "free-one:" port-one "free-two:" port-two)
          mu (run-mu {:port port-one :https-port port-two})
          mu-uri-http (.httpUri mu)
          mu-uri-https (.httpsUri mu)
          _ (log/info "mu started here:" (.toString mu-uri-http) "and here:" (.toString mu-uri-https))
          response-http (client/get (.toString mu-uri-http) {:throw-exceptions false})
          response-https (client/get (.toString mu-uri-http) {:insecure? true :throw-exceptions false})
          _ (stop-mu mu)]
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
          _ (log/info "response:" response)
          _ (stop-mu mu-server)]
      (is (= 200 (:status response)))
      (is (= "Hello, World!" (:body response))))))


(deftest clojure-like-request-thing
  (testing "for a basic GET call we get back a clojure map as the request"
    (let [mu-builder (configure-mu)
          request-atom (atom nil)
          mu-server (-> mu-builder
                        (GET "/" (fn [request]
                                   (do
                                     (reset! request-atom request)
                                     {:status 200 :body "Hello, World!"})))
                        (start-mu))
          mu-uri (.uri mu-server)
          response (client/get (.toString mu-uri))
          _ (stop-mu mu-server)
          _ (log/info "request:" @request-atom)]
      (is (= 200 (:status response)))
      (is (= "GET" (:method @request-atom)))
      (is (= "HTTP/1.1" (:protocol @request-atom)))
      (is (= "" (:context-path @request-atom)))
      (is (= "close" (:Connection (:headers @request-atom))))
      (is (clojure.string/includes? (:uri @request-atom) "localhost"))
      (is (= {} (:path-params @request-atom)))
      )))

(deftest check-params-on-url
  (testing "for a basic GET call we get back a clojure map as the request"
    (let [mu-builder (configure-mu)
          request-atom (atom nil)
          mu-server (-> mu-builder
                        (GET "/{name}/{blah}" (fn [request]
                                                (do
                                                  (reset! request-atom request)
                                                  {:status 200 :body "Hello, World!"})))
                        (start-mu))
          mu-uri (.uri mu-server)
          response (client/get (str (.toString mu-uri) "/rajshahuk/shouldbeblah"))
          _ (stop-mu mu-server)
          _ (log/info "request:" @request-atom)]
      (is (= 200 (:status response)))
      (is (= {:name "rajshahuk" :blah "shouldbeblah"} (:path-params @request-atom)))
      )))

(deftest check-query-params-on-url
  (testing "for a basic GET call we get back a clojure map as the request"
    (let [mu-builder (configure-mu)
          request-atom (atom nil)
          mu-server (-> mu-builder
                        (GET "/" (fn [request]
                                   (do
                                     (reset! request-atom request)
                                     {:status 200 :body "Hello, World!"})))
                        (start-mu))
          mu-uri (.uri mu-server)
          response (client/get (str (.toString mu-uri) "/?name=rajshahuk&something=blah&something=blah2"))
          _ (log/info "request:" @request-atom)
          _ (stop-mu mu-server)]
      (is (= 200 (:status response)))
      (is (= {:name ["rajshahuk"] :something ["blah" "blah2"]} (:query-params @request-atom)))
      )))

(deftest check-setting-headers-on-response
  (testing "set some headers on the response and make sure that it works okay"
    (let [mu-builder (configure-mu)
          request-atom (atom nil)
          mu-server (-> mu-builder
                        (GET "/" (fn [request]
                                   (do
                                     (reset! request-atom request)
                                     {:status  200
                                      :body    "Hello, World!"
                                      :headers {"clj-mu-header"     "best-clojure-web-server-eva"
                                                "x-some-new-header" "value-for-some-new-header"}})))
                        (start-mu))
          mu-uri (.uri mu-server)
          response (client/get (str (.toString mu-uri)))
          _ (log/info "request:" @request-atom)
          _ (stop-mu mu-server)]
      (is (= 200 (:status response)))
      (is (= "best-clojure-web-server-eva" (get (:headers response) "clj-mu-header")))
      (is (= "value-for-some-new-header" (get (:headers response) "x-some-new-header")))
      )))

(deftest check-setting-cookies-on-response
  (testing "set some cookies on the response and make sure that it works okay"
    (let [mu-builder (configure-mu)
          request-atom (atom nil)
          mu-server (-> mu-builder
                        (GET "/" (fn [request]
                                   (do
                                     (reset! request-atom request)
                                     {:status  200
                                      :body    "Hello, World!"
                                      :cookies {"cookie-one" {:value "cookie-one-value"}
                                                "cookie-two" {:path "/somepath" :http-only? "true" :value "cookie-two-value"}}
                                      })))
                        (start-mu))
          mu-uri (.uri mu-server)
          response (client/get (str (.toString mu-uri)))
          _ (stop-mu mu-server)
          cookie-one (get (:cookies response) "cookie-one")
          cookie-two (get (:cookies response) "cookie-two")
          _ (log/info "cookie-one:" cookie-one)
          _ (log/info "cookie-two:" cookie-two)]
      (is (= 200 (:status response)))
      (is (= "cookie-one-value" (:value cookie-one)))
      (is (= "/" (:path cookie-one)))
      (is (false? (:secure cookie-one)))
      (is (= "/somepath" (:path cookie-two))))))

(deftest test-static-file-server
  (testing "to ensure that we can create a web server that services static files"
    (let [mu-builder (configure-mu)
          mu-server (-> mu-builder
                        (STATIC "test/resources" "resources")
                        (start-mu))
          mu-uri (.uri mu-server)
          response-one (client/get (str (.toString mu-uri)))
          response-two (client/get (str (.toString mu-uri) "/subdirectory/blah.html"))
          response-three (client/get (str (.toString mu-uri) "/favicon.ico") {:throw-exceptions false})
          _ (stop-mu mu-server)]
      (is (= 200 (:status response-one)))
      (is (clojure.string/includes? (:body response-one) "Welcome to clj-mu"))
      (is (= 200 (:status response-two)))
      (is (clojure.string/includes? (:body response-two) "Blah!"))
      (is (= 404 (:status response-three)))
      )))

(deftest test-serving-things-from-a-context
  (testing "to ensure that we can create a web server that services static files"
    (let [mu-builder (configure-mu)
          mu-server (-> mu-builder
                        (CONTEXT-> "/new-path"
                                   (STATIC "test/resources" "resources")
                                   (GET "/hello-world" (fn [request] {:status 200 :body "Hello, World!"})))
                        (start-mu))
          mu-uri (.uri mu-server)
          response-one (client/get (str (.toString mu-uri)) {:throw-exceptions false})
          response-two (client/get (str (.toString mu-uri) "/new-path/subdirectory/blah.html"))
          response-thr (client/get (str (.toString mu-uri) "/new-path/hello-world"))
          _ (stop-mu mu-server)]
      (is (= 404 (:status response-one)))
      (is (= 200 (:status response-two)))
      (is (= 200 (:status response-thr)))
      (is (clojure.string/includes? (:body response-two) "Blah!"))
      (is (= "Hello, World!" (:body response-thr)))
      )))

(deftest test-serving-things-from-a-context
  (testing "to ensure that we can create a web server that services static files"
    (let [mu-builder (configure-mu)
          mu-server (-> mu-builder
                        (GET "/something" (fn [request] {:status 200 :body "No context"}))
                        (CONTEXT-> "api"
                                   (GET "/something" (fn [request] {:status 200 :body "First level context"}))
                                   (CONTEXT-> "nested"
                                              (GET "/something" (fn [request] {:status 200 :body "Nested context"}))))
                        (start-mu))
          mu-uri (.uri mu-server)
          response-one (client/get (str (.toString mu-uri) "/something") {:throw-exceptions false})
          response-two (client/get (str (.toString mu-uri) "/api/something"))
          response-thr (client/get (str (.toString mu-uri) "/api/nested/something"))
          _ (stop-mu mu-server)]
      (is (= 200 (:status response-one)))
      (is (= 200 (:status response-two)))
      (is (= 200 (:status response-thr)))
      (is (clojure.string/includes? (:body response-one) "No context"))
      (is (clojure.string/includes? (:body response-two) "First level context"))
      (is (clojure.string/includes? (:body response-thr) "Nested context"))
      )))

(deftest test-form-parameters
  (testing "to ensure that nil is returned when there is no form"
    (let [mu-builder (configure-mu)
          mu-server (-> mu-builder (POST "/something"
                                         (fn [request]
                                           {:status 200 :body (forms request)}))
                        (start-mu))
          mu-uri (.uri mu-server)
          response (client/post (str (.toString mu-uri) "/something") {:throw-exceptions false})
          _ (stop-mu mu-server)]
      (is (= 200 (:status response)))
      (is (= "" (:body response)))
      ))
  (testing "to ensure that all form params are available"
    (let [mu-builder (configure-mu)
          mu-server (-> mu-builder (POST "/something"
                                         (fn [request]
                                           {:status 200
                                            :body   (cheshire.core/generate-string (forms request))}))
                        (start-mu))
          mu-uri (.uri mu-server)
          response (client/post (str (.toString mu-uri) "/something") {:form-params      {:foo  "bar"
                                                                                          "foo" "bars"
                                                                                          :cat  "dog"}
                                                                       :throw-exceptions false})
          _ (stop-mu mu-server)]
      (is (= 200 (:status response)))
      (is (= "{\"foo\":[\"bar\",\"bars\"],\"cat\":[\"dog\"]}" (:body response))))))

(deftest read-body-of-post-as-string
  (testing "to see if we can read the body of a post as a string"
    (let [mu-builder (configure-mu)
          mu-server (-> mu-builder (POST "/something"
                                         (fn [request]
                                           {:status 200
                                            :body   (body request)}))
                        (start-mu))
          mu-uri (.uri mu-server)
          test-payload {:foo  "bar"
                 "foo" "bars"
                 :cat  "dog"}
          response (client/post (str (.toString mu-uri) "/something") {:body             (cheshire.core/generate-string test-payload)
                                                                       :throw-exceptions false})
          _ (stop-mu mu-server)]
      (is (= 200 (:status response)))
      (is (= (cheshire.core/generate-string test-payload) (:body response))))))


;;
;; commented out for now
;;
;(deftest make-double-read
;  (testing "to see if you can read the body if you have already read via forms"
;    (let [mu-builder (configure-mu)
;          mu-server (-> mu-builder (POST "/something"
;                                         (fn [request]
;                                           (let [forms (forms request)]
;                                             (try
;                                               {:status 200
;                                                :body   (body request)}
;                                               (catch IllegalStateException e
;                                                 {:status 500
;                                                  :body "Correctly caught exception"}
;                                                 ))
;
;                                             )
;                                           ))
;                        (start-mu))
;          mu-uri (.uri mu-server)
;          test-payload {:foo  "bar"
;                        "foo" "bars"
;                        :cat  "dog"}
;          response (client/post (str (.toString mu-uri) "/something") {:body             (cheshire.core/generate-string test-payload)
;                                                                       :throw-exceptions false})
;          _ (stop-mu mu-server)]
;      (is (= 500 (:status response)))
;      (is (= "Correctly caught exception" (:body response))))))

(deftest cookies-are-available-on-the-request
  (let [mu-builder (configure-mu)
        cookie-holder (atom nil)
        cookie-store (clj-http.cookies/cookie-store)
        mu-server (-> mu-builder
                      (POST "/set-a-cookie"
                            (fn [request]
                              {:status  200
                               :body    "Hello, World!"
                               :cookies {"cookie-one" {:value "cookie-one-value"}
                                         "cookie-two" {:path "/somepath" :http-only? "true" :value "cookie-two-value"}}
                               }))
                      (GET "/check-to-see-cookie-can-be-read"
                           (fn [request]
                             (do
                               (reset! cookie-holder (:cookies request))
                               {:status 200
                                :body   ""
                                })))
                      (start-mu)
                      )
        _ (client/post (str (.toString (.uri mu-server)) "/set-a-cookie") {:cookie-store cookie-store})
        _ (client/get (str (.toString (.uri mu-server)) "/check-to-see-cookie-can-be-read") {:cookie-store cookie-store})
        _ (stop-mu mu-server)]
    (log/info "cookie-holder" @cookie-holder)
    (is (not (nil? @cookie-holder)))
    (is (= "cookie-one" (-> @cookie-holder first :name)))
    )
  )

(deftest path-parameters
  (let [mu-builder (configure-mu)
        path-param (atom nil)
        mu-server (-> mu-builder
                      (POST "/postparam/{someparam}"
                            (fn [request]
                              (log/info "path-param" (:path-params request))
                              (reset! path-param (-> request :path-params :someparam))
                              {:status 200
                               :body @path-param
                               }))
                      (start-mu))
        _ (client/post (str (.toString (.uri mu-server)) "/postparam/blah" ))
        _ (stop-mu mu-server)]
    (log/info "path-param" @path-param)
    (is (= "blah" @path-param))
    ))