(ns appengine-clj.datastore-test
  (:require [appengine-clj.datastore :as ds])
  (:use
     clojure.contrib.test-is
     appengine-clj.test-utils)
  (:import (com.google.appengine.api.datastore
             DatastoreServiceFactory
             Entity
             EntityNotFoundException
             Query
             Query$FilterOperator
             Query$SortDirection)))


(dstest entity-to-map-converts-to-persistent-map
  (let [entity (doto (Entity. "MyKind")
                (.setProperty "foo" "Foo")
                (.setProperty "bar" "Bar"))]
    (.put (DatastoreServiceFactory/getDatastoreService) entity)
    (is (= {:foo "Foo" :bar "Bar" :kind "MyKind" :key (.getKey entity)}
           (ds/entity-to-map entity)))))

(dstest find-all-runs-given-query
  (.put (DatastoreServiceFactory/getDatastoreService)
        [(doto (Entity. "A") (.setProperty "code" 1) (.setProperty "name" "jim"))
         (doto (Entity. "A") (.setProperty "code" 2) (.setProperty "name" "tim"))
         (doto (Entity. "B") (.setProperty "code" 1) (.setProperty "name" "jan"))])
  (is (= ["jim" "tim"] (map :name (ds/find-all (doto (Query. "A") (.addSort "code"))))))
  (is (= ["tim" "jim"] (map :name (ds/find-all (doto (Query. "A") (.addSort "code" Query$SortDirection/DESCENDING))))))
  (is (= ["jim"] (map :name (ds/find-all (doto (Query. "A") (.addFilter "code" Query$FilterOperator/EQUAL 1))))))
  (is (= ["jan"] (map :name (ds/find-all (doto (Query. "B") (.addFilter "code" Query$FilterOperator/EQUAL 1)))))))

(dstest create-saves-and-returns-item-with-a-key
  (let [created-item (ds/create {:kind "MyKind" :name "hume" :age 31})]
    (is (not (nil? (created-item :key))))
    (let [created-entity (.get (DatastoreServiceFactory/getDatastoreService) (created-item :key))]
      (is (= "MyKind" (.getKind created-entity)))
      (is (= "hume" (.getProperty created-entity "name")))
      (is (= 31 (.getProperty created-entity "age"))))))

(dstest create-can-create-a-child-entity-from-a-parent-key
  (let [parent (ds/create {:kind "Mother" :name "mama"})
        child (ds/create {:kind "Child" :name "baby"} (parent :key))]
    (is (= (parent :key) (.getParent (child :key))))
    (is (= [child] (ds/find-all (doto (Query. "Child" (parent :key))))))))

(dstest get-given-a-key-returns-a-mapified-entity
  (let [key (:key (ds/create {:kind "Person" :name "cliff"}))]
    (is (= "cliff" ((ds/get key) :name)))))

(dstest delete-by-key
  (let [key (:key (ds/create {:kind "MyKind"}))]
    (ds/delete key)
    (is (thrown? EntityNotFoundException (ds/get key)))))

(dstest delete-by-multiple-keys
  (let [key1 (:key (ds/create {:kind "MyKind"}))
        key2 (:key (ds/create {:kind "MyKind"}))]
    (ds/delete key1 key2)
    (are (thrown? EntityNotFoundException (ds/get _1))
         key1
         key2)))

