(ns appengine-clj.datastore)


(defn entity-to-map
  "Converts an instance of com.google.appengine.api.datastore.Entity
  to a PersistentHashMap with properties stored under keyword keys,
  plus the entity's kind stored under :kind."
  [entity]
  (reduce #(assoc %1 (keyword (key %2)) (val %2))
    {:kind (.getKind entity)}
    (.entrySet (.getProperties entity))))

