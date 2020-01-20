# clj-mu

[![Build Status](https://api.travis-ci.org/rajshahuk/clj-mu.png?branch=master)](http://travis-ci.org/rajshahuk/clj-mu)

**Note: Use this at your peril, this is a very very early version of the API and it is likely to be change. It has been
built to 'scratch my own itch'**

Clojure friendly library for using https://muserver.io

This library has been written to allow more a _clojure-ey_ way to use MuServer. It has been written
with a view to keep dependencies down to a minimum and as a result does not try to compatible with
other popular frameworks such as ring or compojure. This library has however been heavily inspired
by them after more than a year of building services with them.

### Get started quickly

#### Installation

[![Clojars Project](https://img.shields.io/clojars/v/com.twelvenines/clj-mu.svg)](https://clojars.org/com.twelvenines/clj-mu)

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

The following request types are supported:
   * GET
   * POST
   * HEAD
   * PUT
   * DELETE
   
The way to define a route is like this `(METHOD PATH HANDLER)` e.g.
```clojure
(POST "/submit" handler)
```

A request handler is defined like this:

```clojure
(fn [request]
  ;; body of request
  ;; response is returned as map as per below "Responses" section
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

#### Path Contexts

muserver has a nice way to define path contexts and this is ported over to clj-mu. The documentation on muserver
is here: https://muserver.io/contexts

Let's take the 3 paths in the muserver docus and see how they would be implemented in clj-mu

   * `/something`
   * `/api/something`
   * `/api/nested/something`
   
```clojure
(let [mu-builder (configure-mu)
          mu-server (-> mu-builder
                        (GET "/something" (fn [request] {:status 200 :body "No context"}))
                        (CONTEXT-> "api"
                           (GET "/something" (fn [request] {:status 200 :body "First level context"}))
                           (CONTEXT-> "nested"
                              (GET "/something" (fn [request] {:status 200 :body "Nested context"}))))
                        (start-mu))]
  (println (str "Static web server started here: "(.uri mu-server))))
```


### Responses

Responses are always returned as a map. The map has the following mandatory keys:

   * status - http status code
   * body - body to be returned
   
The follow are optional:

   * headers - http headers that will be added to the response. These are key/value pairs as you would have
    in the http response
   * cookies - a map of one or more maps, as per the example below
   
#### Example

```clojure
{ 
    :status 200
    :body   "Hello, World"
    :headers {
      "content-type" "text/plain" ;; any set of key-value pairs
    }
    :cookies {
       "name-of-the-cookie" {
          :value "the-value-for-my-cookie" ;; required value - without this you can't create a cookie.
                                           ;; values are ALWAYS url encoded!
          :path "/" ;; optional value - defaults to "/"
          :secure true ;; optional defaults to false
          :http-only true ;; defaults to false
        }
    } 
}
```

### Special Handlers

#### Hosting static content

muserver has a really good way to handle static content, see the documentation here for more info:
https://muserver.io/resources

clj-mu provides a wrapper around this and defined in the `STATIC` function. The function takes two arguments, one for
the file path and the other for the classpath. This is useful when running locally and when bundled as an uber-jar.

```clojure
(let [mu-builder (configure-mu)
          mu-server (-> mu-builder
                        (STATIC "test/resources" "resources")
                        (start-mu))]
      (println (str "Static web server started here: "(.uri mu-server))))
```

## TODO LIST

- HEAD and PUT requests
- File Upload support
- Async!
- More tests for unhappy paths
- ~~Write tests for sending headers on response~~
- ~~Implement cookies~~
- ~~Add the ability use contextPaths~~
- ~~Simple implementation for static files~~
- ~~Extracting form params~~