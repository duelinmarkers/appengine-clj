(ns appengine-clj.users
  (:import
    (com.google.appengine.api.users User UserService UserServiceFactory)))


(defn user-info []
  (let [user-service (UserServiceFactory/getUserService)]
    {:user (.getCurrentUser user-service) :user-service user-service}))

(defn wrap-with-user-info [application]
  (fn [request]
    (application (assoc request :appengine-clj/user-info (user-info)))))

