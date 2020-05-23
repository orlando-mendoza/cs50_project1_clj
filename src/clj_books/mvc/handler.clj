(ns clj-books.mvc.handler
  (:require [clj-books.mvc.services :refer [get-good-reads-review
                                            get-book-cover]]
            [clj-books.mvc.model :as model]
            [clj-books.validators.user-validator :as v])
  (:require [clojure.data.json :as json]
            [clojure.walk :refer [keywordize-keys]]
            [buddy.hashers :as hashers]
            [ring.util.response :refer [response redirect]]
            [selmer.parser :as parser])
  (:gen-class))

(defn handle-index [req]
  (let [db (:clj-books/db req)
        books (model/select-books db)]
    (parser/render-file "templates/index.html" {:session (:session req) :books books})))

(defn handle-search-index [req]
  (let [db (:clj-books/db req)
        search-by (get-in req [:form-params "search-by"])
        search-text (get-in req [:form-params "search-text"])
        books (model/select-books db search-by search-text)]
    (parser/render-file "templates/index.html" {:session (:session req)
                                                :books books})))

(defn handle-book-page [req]
  (let [db (:clj-books/db req)
        search-by "isbn"
        search-text (get-in req [:params :isbn])
        isbn (get-in req [:params :isbn])
        book (first (model/select-books db search-by search-text))
        book-cover (get-book-cover isbn)
        book-ratings (get-good-reads-review isbn)
        ratings-count (get book-ratings :work_ratings_count)
        avg-rating (get book-ratings :average_rating)
        reviews (model/select-reviews db isbn)]
    (parser/render-file "templates/isbn.html" {:session (:session req)
                                               :book book
                                               :ratings-count ratings-count
                                               :avg-rating avg-rating
                                               :cover book-cover
                                               :reviews reviews
                                               :flash (:flash req)
                                               :selmer/context "/"})))

(defn handle-create-review [req]
  (let [form (:form-params req)
        session (:session req)
        db (:clj-books/db req)
        {:strs [rate review]} form
        isbn (get-in req [:route-params :isbn])
        id (:id session)]
    (if (not (nil? id))
      ;; If user is logged in
      ;; Checks if user has submitted a review previously
      (if (= (:count (first (model/select-reviews-by-user-id db id isbn))) 0)
        (do
          (model/insert-review db (Integer/parseInt rate) review id isbn)
          (-> (redirect (str "/isbn/" isbn))
              (merge {:flash {"success"
                              "Thanks for your review"}})))
        (-> (redirect (str "/isbn/" isbn))
            (merge {:flash {"danger"
                            "You have already reviewed this book. Only one review is permitted"}})))
      ;; If user is not logged in
      (-> (redirect (str "/isbn/" isbn))
          (merge  {:flash {"warning"
                           "You need to log in to write a review"}})))))

(defn handle-api-isbn [req]
  (let [db (:clj-books/db req)
        search-by "isbn"
        search-text (get-in req [:params :isbn])
        isbn (get-in req [:params :isbn])
        book (first (model/select-books db search-by search-text))]
    (if (empty? book)
      (compojure.route/not-found "ISBN Not Found")
      (do
        (let [book-ratings (get-good-reads-review isbn)
              ratings-count (get book-ratings :work_ratings_count)
              avg-rating (get book-ratings :average_rating)]
          {:headers {"Content-type" "application/json"}
           :status 200
           :body (json/write-str {:title (:title book),
                                  :author (:author book),
                                  :year (:year book),
                                  :isbn isbn,
                                  :review_count ratings-count,
                                  :average_score avg-rating})})))))


(defn login [req]
  (parser/render-file "templates/login.html" {:title "Login"}))

(defn login-submit [{form    :form-params
                     session :session
                     :as     req}]
  (let [{:strs [email password]} form
        db (:clj-books/db req)]
    (if-let [user (model/login-user db email)]
      (if (hashers/check password (:password user))

        ;; If authenticated
        (let [next-session (assoc session :session (dissoc user :password))]
          (-> (redirect "/")
              (merge next-session)))

        ;; Otherwise
        (parser/render-file "templates/login.html" {:flash
                                                    {"danger"
                                                     "Incorrect password"}}))
      (parser/render-file "templates/login.html" {:flash
                                                  {"danger"
                                                   "Email incorrect!"}}))))

(defn logout [req]
  (-> (redirect "/")
      (merge {:session nil})))

(defn register [req]
  (let [current-user (get-in req [:session :current-user])]
    (parser/render-file "templates/register.html"
                        {:title        "Register"
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
           {:flash {"danger" "Email already registered, please provide another email!"}})
          ;; inserts the new user in the database
          (let [user-id (model/register-user! db first-name last-name email password)
                books (model/select-books db)]
            (parser/render-file
             "templates/index.html"
             {:title    ""
              :flash {"success" "Your are successfully registered!"
                      "warning" "Please login to write a review"}
              :books    books}))))
      (parser/render-file "templates/register.html" (assoc form :errors errors)))))
