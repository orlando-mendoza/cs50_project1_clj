(ns clj-books.mvc.handler
  (:require [clj-books.mvc.model :as model]
            [selmer.parser :as parser]
            [clj-books.config :refer [db-spec]]
            [clojure.java.jdbc :as jdbc]
            [ring.util.response :refer [redirect]]
            [clj-books.validators.user-validator :as v]
            [clojure.walk :refer [keywordize-keys]]
            [buddy.hashers :as hashers])
  (:gen-class))

(defn handle-index [req]
  (let [db (:clj-books/db req)
        books (model/select-books db)]
    (parser/render-file "templates/index.html" {:session (:session req) :books books})))

(defn handle-search-index [req]
  (let [username (get-in req [:session :username])
        db (:clj-books/db req)
        search-by (get-in req [:form-params "search-by"])
        search-text (get-in req [:form-params "search-text"])
        books (model/select-books db search-by search-text)]
    (parser/render-file "templates/index.html" {:session (:session req) :books books})))

(defn handle-isbn-details [req]
  ;; TODO
  {:status 200
   :headers {}
   :body ""})

(defn handle-create-review [req]
  ;; TODO
  {:status 200
   :headers {}
   :body ""})

(defn handle-api-isbn [req]
  {:status 200
   :headers {}
   :body ""})

(defn login [req]
  (parser/render-file "templates/login.html" {:title "Login"}))

(defn login-submit [{form :form-params
                     session :session
                     :as req}]
  (let [{:strs [email password]} form
        db (:clj-books/db req)]
    (if-let [user (model/login-user db email)]
      (if (hashers/check password (:password user))

        ;; If authenticated
        (let [next-session (assoc session :session (dissoc user :password))]
          (-> (redirect "/")
              (merge next-session)))

        ;; Otherwise
        (parser/render-file "templates/login.html" {:messages
                                                    {"danger"
                                                     "Incorrect password"}}))
      (parser/render-file "templates/login.html" {:messages
                                                  {"danger"
                                                   "Email incorrect!"}}))))

(defn logout [req]
  (-> (redirect "/")
      (merge {:session nil})))

(defn register [req]
  (let [current-user (get-in req [:session :current-user])]
    (parser/render-file "templates/register.html"
                        {:title "Register"
                         :current-user current-user})))

(defn register-submit [{form :form-params :as req}]
  (let [errors (v/validate-signup (keywordize-keys form))
        {:strs [first-name last-name email password]} form
        db (:clj-books/db req)]
    (if (empty? errors)
      (do
        (if (model/control-user-by-email db email)
          (parser/render-file
           "templates/register.html"
           {:messages {"danger" "Email already registered, please provide another email!"}})
           ;; inserts the new user in the database
          (let [user-id (model/register-user! db first-name last-name email password)
                books (model/select-books db)]
            (parser/render-file
             "templates/index.html"
             {:title ""
              :messages {"success" "Your are successfully registered!"
                         "warning" "Please login to write a review"}
                                                        :books books}))))
      (parser/render-file "templates/register.html" (assoc form :errors errors)))))
