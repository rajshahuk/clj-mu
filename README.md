# clj-mu
--

[![Build Status](https://api.travis-ci.org/rajshahuk/clj-mu.png?branch=master)](http://travis-ci.org/rajshahuk/clj-mu)

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

TODO

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