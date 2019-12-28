(ns clj-mu.core
  (:require
    [clojure.tools.logging :as log])
  (:import (io.muserver MuServer MuServerBuilder)))


(defn ^MuServer run-mu
  "start mu-server with bunch of options. If none are passed through mu will start an http server on a free port.
  You can find the port by using (.uri mu-server-instance)

  options need to be passed in as a map as follows

  :port     - http port to start up on"
  ([]
   (run-mu {}))
  ([options]
  (let [mu-builder (cond-> (MuServerBuilder/httpServer)
                   (integer? (:port options)) (.withHttpPort (:port options))
               )]
    (try
      (.start mu-builder)
      (catch Exception e
        (do
          (log/error "unable to start mu-server" e) e))))))