(ns appengine-clj.test-utils
  (:require [clojure.contrib.test-is :as test-is])
  (:import
    (com.google.appengine.tools.development ApiProxyLocalFactory)
    (com.google.appengine.api.datastore.dev LocalDatastoreService)
    (com.google.apphosting.api ApiProxy)))


(defn ds-setup []
  (let [proxy-factory (doto (ApiProxyLocalFactory.) (.setApplicationDirectory (java.io.File. ".")))
        api-proxy     (doto (.create proxy-factory) (.setProperty LocalDatastoreService/NO_STORAGE_PROPERTY "true"))]
    (ApiProxy/setDelegate api-proxy))
  (ApiProxy/setEnvironmentForCurrentThread
    (proxy [com.google.apphosting.api.ApiProxy$Environment] []
      (getAppId [] "test")
      (getVersionId [] "1.0")
      (getRequestNamespace [] "")
      (getAttributes [] (java.util.HashMap.)))))

(defn ds-teardown []
  (ApiProxy/clearEnvironmentForCurrentThread)
  (.stop (ApiProxy/getDelegate)))

(defmacro dstest [name & body]
  `(test-is/deftest ~name
    (ds-setup)
    (try
      ~@body
      (finally (ds-teardown)))))

