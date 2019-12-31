(ns clj-mu.core
  (:require
    [clojure.tools.logging :as log])
  (:import (io.muserver MuServer MuServerBuilder RouteHandler Method MuRequest MuResponse CookieBuilder)))

(defn extract-request
  "return more of a clojure style request object back so that the handler can be more clojure like"
  [^MuRequest request params]
  {
   :original-mu-request request
   :method              (.name (.method request))
   :protocol            (.protocol request)
   :remote-address      (.remoteAddress request)
   :relative-path       (.relativePath request)
   :context-path        (.contextPath request)
   :uri                 (.toString (.uri request))
   :path-params         (clojure.walk/keywordize-keys (into {} params))
   :query-params        (reduce (fn [a v]
                                  (assoc a
                                    (keyword (.getKey v))
                                    (into [] (.getValue v))
                                    )) {} (iterator-seq (.iterator (.entrySet (.all (.query request))))))
   :headers             (reduce (fn [a v]
                                  (assoc a
                                    (keyword (.getKey v))
                                    (.getValue v)
                                    )) {} (iterator-seq (.iterator (.headers request))))
   }
  )

(defn add-cookies
  "add cookies to the response
   cookies are a map with the key as the name of the cookie and the value as as the cookie details"
  [^MuResponse response cookies]
  (doseq [[k v] cookies]
    (log/info "cookies" k v)
    (.addCookie response
                (.build
                  (cond-> (CookieBuilder/newCookie)
                          true (.withName k)
                          true (.withUrlEncodedValue (:value v))
                          (string? (:path v)) (.withPath (:path v))
                          (boolean? (:secure v)) (.secure (:secure v))
                          (boolean? (:http-only v)) (.httpOnly (:http-only v))
                          )))))

(defn- add-headers
  "add headers to the response"
  [^MuResponse response headers]
  (let [hdrs (.headers response)]
    (doseq [[k, v] headers]
      (.set hdrs (name k) v)))
  )

(defn create-route-handler [handler]
  (reify RouteHandler
    (handle [_ req res params]
      (let [request (extract-request req params)
            {body :body headers :headers status :status cookies :cookies} (handler request)]
        (.status res status)
        (when headers (add-headers res headers))
        (when cookies (add-cookies res cookies))
        (.write res body)))))

(defn GET [mu-builder path handler]
  (.addHandler mu-builder Method/GET path (create-route-handler handler)))

(defn ^MuServerBuilder configure-mu
  "configure mu-server with bunch of options. If no options are passed in mu will start an http server on a free port.
  This function does not start mu-server. See the start-mu function to start mu-server.

  options need to be passed in as a map as follows

  :port           - http port to start up on
  :https-port     - https port to start up on"
  ([] (configure-mu {}))
  ([options]
   (let [mu-builder (cond-> (MuServerBuilder/httpServer)
                            (integer? (:port options)) (.withHttpPort (:port options))
                            (integer? (:https-port options)) (.withHttpsPort (:https-port options)))]
     mu-builder)))

(defn start-mu
  "starts mu-server"
  [^MuServerBuilder mu-server-builder]
  (.start mu-server-builder))

(defn ^MuServer run-mu
  "helper function to help with some testing"
  ([]
   (run-mu {}))
  ([options]
   (let [mu-builder (configure-mu options)]
     (try
       (do
         (start-mu mu-builder))
       (catch Exception e
         (do
           (log/error "unable to start mu-server" e)
           (throw e)))))
   ))