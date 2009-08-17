(ns appengine-clj.users-test
  (:require [appengine-clj.users :as users])
  (:use clojure.contrib.test-is))


(deftest user-info-given-request
  (let [user-info-map {:user "instance of User" :user-service "instance of UserService"}
        ring-request {:appengine-clj/user-info user-info-map}]
    (is (= (users/user-info ring-request) user-info-map))))

(deftest wrap-requiring-login
  (testing "redirects to login when user isn't logged in"
    (let [fake-user-service (proxy [com.google.appengine.api.users.UserService] []
                              (isUserLoggedIn [] false)
                              (createLoginURL [dest] (str "/login?then=" dest)))
          request {:appengine-clj/user-info {:user nil :user-service fake-user-service}}]
      (let [wrapped-app-with-url (users/wrap-requiring-login #(throw (Exception.)) "/the_path")]
        (is (= {:status 302 :headers {"Location" "/login?then=/the_path"}}
               (wrapped-app-with-url request))))
      (let [wrapped-app-with-no-url (users/wrap-requiring-login #(throw (Exception.)))]
        (is (= {:status 302 :headers {"Location" "/login?then=/"}}
               (wrapped-app-with-no-url request))))))
  (testing "allows request to pass when user is logged in"
    (let [fake-user-service (proxy [com.google.appengine.api.users.UserService] []
                              (isUserLoggedIn [] true))
          request {:appengine-clj/user-info {:user "instance of User" :user-service fake-user-service}}
          dummy-response {:status 200 :body "Hello"}
          dummy-app (fn [request] dummy-response)]
      (let [wrapped-app-with-url (users/wrap-requiring-login dummy-app "/the_path")]
        (is (= dummy-response (wrapped-app-with-url request))))
      (let [wrapped-app-with-no-url (users/wrap-requiring-login dummy-app)]
        (is (= dummy-response (wrapped-app-with-no-url request)))))))

