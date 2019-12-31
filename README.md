# clj-mu
--

[![Build Status](https://api.travis-ci.org/rajshahuk/clj-mu.png?branch=master)](http://travis-ci.org/rajshahuk/clj-mu)

**Note: Use this at your peril, this is a very very early version of the API and it is likely to be change. It has been
built to 'scratch my own itch'**

Clojure friendly library for using https://muserver.io

This library has been written to allow more a _clojure-ey_ way to use MuServer. It has been written
with a few to keep dependencies down to a minimum and as a result does not try to compatible with
other popular frameworks such as ring or compojure. This library has however been heavily inspired
by them after more than a year of building services with them.

### Get started quickly

Add clj-mu as a dependency to your project and then add the following to your `require` definition
```clojure
[clj-mu.core :refer :all]
```

start and run a basic mu-server like this:
```clojure
(let [mu-builder (configure-mu)
      mu-server (-> mu-builder
                    (GET "/" (fn [request] {:status 200 :body "Hello, World!"}))
                    (start-mu))]
      (println (str "Mu started here: " (.toString (.uri mu-server)))))
```

### Requests

A request handler is defined like this:

```clojure
(fn [request]
  ;; body of request
  ;; response is returned as map as per below "Response section
  ) 
```

The `request` parameter passed into the function has the following key/values:

```clojure
{
   :original-mu-request ORIGINAL_MU_REQUEST_IF_YOU_NEED_IT
   :method              HTTP_METHOD ;; e.g. GET
   :protocol            HTTP_PROTOCOL ;; e.g. HTTP/1.1
   :remote-address      REMOTE_IP_ADDRESS_OF_THE_REQUEST
   :relative-path       RELATIVE_PATH_WITHOUT_QUERY_PARAMS
   :context-path        RETURNS_CONTEXT_PATH_IF_USED
   :uri                 FULL_URI_OF_THE_REQUEST
   :path-params         PATH_PARAMETERS_AS_A_MAP
   :query-params        QUERY_PARAMETERS_AS_A_MAP ;; n.b. values are in a list as there
                                                  ;; could be more than one for each key
   :headers             HEADERS_AS_A_MAP
}
```

### Responses

Responses are always returned as a map. The map has the following mandatory keys:

   * status - http status code
   * body - body to be returned
   
The follow are optional:

   * headers - standard http headers that will be added to the response
   
#### Example

```clojure
{ 
    :status 200
    :body   "Hello, World"
    :headers {
      "content-type" "text/plain"
    }
}
```