(ns appengine-clj.datastore-test
  (:require [appengine-clj.datastore :as ds])
  (:use
     clojure.contrib.test-is
     appengine-clj.test-utils)
  (:import (com.google.appengine.api.datastore Entity DatastoreServiceFactory)))


(dstest converts-entity-to-persistent-map
  (let [entity (doto (Entity. "MyKind")
                (.setProperty "foo" "Foo")
                (.setProperty "bar" "Bar"))]
    (.put (DatastoreServiceFactory/getDatastoreService) entity)
    (is (= {:foo "Foo" :bar "Bar" :kind "MyKind" :key (.getKey entity)}
           (ds/entity-to-map entity)))))

