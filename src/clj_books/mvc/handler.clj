(ns clj-books.mvc.handler
  (:require [clj-books.mvc.model :as model]
            [selmer.parser :as parser]
            [clj-books.config :refer [db-spec]]
            [clojure.java.jdbc :as jdbc]
            [ring.util.response :as response]
            [clj-books.validators.user-validator :as v]
            [clojure.walk :refer [keywordize-keys]])
  (:gen-class))

(defn handle-index [req]
  (let [db (:clj-books/db req)
        books (model/select-books db)
        username (get-in req [:session :username])]
    (parser/render-file "templates/index.html" {:current-user username :books books})))

(defn handle-search-index [req]
  (let [username (get-in req [:session :username])
        db (:clj-books/db req)
        search-by (get-in req [:form-params "search-by"])
        search-text (get-in req [:form-params "search-text"])
        books (model/select-books db search-by search-text)]
    (parser/render-file "templates/index.html" {:current-user username :books books})))

(defn handle-isbn-details [req]
  ;; TODO
  {:status 200
   :headers {}
   :body ""}
  )

(defn handle-create-review [req]
  ;; TODO
  {:status 200
   :headers {}
   :body ""}
  )

(defn handle-api-isbn [req]
  {:status 200
   :headers {}
   :body ""}
  )

(defn login [req]
  (parser/render-file "login.html" {:title "Login"}))

(defn register [req]
  #_(clojure.pprint/pprint req)
  (let [current-user (get-in req [:session :current-user])]
    (parser/render-file "templates/register.html" {:title "Register" :current-user current-user})))

(defn register-submit [{form :form-params :as req}]
  (let [errors (v/validate-signup (keywordize-keys form))
        {:strs [first-name last-name email password]} form
        db (:clj-books/db req)]
    (if (empty? errors)
      (do
        ;; inserts the new user in the database
        (let [user-id (model/register-user! db first-name last-name email password)
              books (model/select-books db)]
          (parser/render-file "templates/index.html" {:title ""
                                                      :messages {"success" "Your are successfully registered!"
                                                                 "warning" "Please login to write a review"}
                                                      :books books})))
      (parser/render-file "templates/register.html" (assoc form :errors errors)))))


(comment #_(let [params (get-in (:params req))
                 {:keys [first-name
                         last-name
                         email
                         password
                         confirm-password]} params]
             ((if not = (password confirm-password))
              (parser/render-file "templates/register.html" {:title "Register"
                                                             :messages ["Password entered doesn't match", "danger"] })

              ;; if passwords do match then register the user
              (let [db (:webdev/db req)]
                (if (> (model/control-user-by-email db email) 0)
                  (parser/render-file "templates/register.html" {:title "Register"
                                                                 :messages ["Email already registered, please try again", "danger"]})
                  (let [user-id (model/register-user! db first-name last-name email password)]
                    (-> (response/redirect "/" {:current-user first-name
                                                :message ["You have successfuly registered, now you are logged in!", "success"]})
                        (assoc :session {:current-user first-name
                                         :user-id user-id}))))))))
