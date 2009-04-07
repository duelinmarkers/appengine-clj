(ns appengine-clj.users-test
  (:require [appengine-clj.users :as users])
  (:use clojure.contrib.test-is))

(comment
(deftest wrap-requiring-login
  (testing "redirects to login when user isn't logged in"
    (let [fake-user-service (proxy [com.google.appengine.api.users.UserService] []
                              (isUserLoggedIn [] false)
                              (createLoginURL [dest] (str "/login?then=" dest)))
          request {:appengine-clj/user-info {:user nil :user-service fake-user-service}}]
      (is (= {:status 302 :headers {"Location" "/login?then=/path"}}
             ((users/wrap-requiring-login #(throw (Exception.)) request)))))))
)

