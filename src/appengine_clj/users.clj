(ns appengine-clj.users
  (:import
    (com.google.appengine.api.users User UserService UserServiceFactory)))


(defn user-info
  "Returns a UserService and User for the current request in a map keyed by :user-service and :user respectively.
  if the user is not logged in, :user will be nil."
  []
  (let [user-service (UserServiceFactory/getUserService)]
    {:user (.getCurrentUser user-service) :user-service user-service}))

(defn wrap-with-user-info
  "Ring middleware method that wraps an application so that every request will have
  a user-info map assoc'd to the request under the key :appengine-clj/user-info."
  [application]
  (fn [request]
    (application (assoc request :appengine-clj/user-info (user-info)))))

