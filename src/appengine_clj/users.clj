(ns appengine-clj.users
  (:import
    (com.google.appengine.api.users User UserService UserServiceFactory)))


(defn wrap-with-user-info [application]
  (fn [request]
    (let [user-service (UserServiceFactory/getUserService)
          user (.getCurrentUser user-service)]
      (application (assoc request :appengine-clj/user-info {:user user :user-service user-service})))))

