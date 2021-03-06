(ns clj-books.mvc.model
  (:require [clojure.java.jdbc :as jdbc]
            [buddy.hashers :as hashers])
  (:gen-class))

(defn control-user-by-email
  "Check if under registration the email already exists in the database returning true for success and false for failure"
  [db email]
  (not= 0 (apply int (map :count (jdbc/query db ["SELECT count(*) FROM users WHERE email = ?" email])))))

(defn select-books
  "returns the list of books according to the params"
  ([db]
   (jdbc/query db ["SELECT * FROM books"]))
  ([db search-by search-text]
   (jdbc/query db [(str "SELECT * FROM books WHERE " search-by " iLIKE '%" search-text "%'")])))

(defn select-reviews
  "returns the reviews associated  with a isbn"
  [db isbn]
  (jdbc/query db ["SELECT first_name, last_name, rate, comment, date, isbn_no
                   FROM reviews LEFT JOIN users ON users.id = reviews.user_id
                   WHERE reviews.isbn_no = ?"
                  isbn]))

(defn select-reviews-by-user-id
  "Given the user-id and the isbn, checks if the user
  has already reviewed this particular book. Cause only
  one review is permitted by user"
  [db user-id isbn]
  (jdbc/query db ["SELECT count(*)
                   FROM reviews
                   WHERE user_id = ?
                   AND isbn_no = ?"
                  user-id
                  isbn]))

(defn insert-review
  "Register the review in the database"
  [db rate comment user_id isbn_no]
  (jdbc/execute! db ["INSERT INTO reviews (rate, comment, user_id, isbn_no, date)
                       VALUES (?, ?, ?, ?, to_date(?, 'DD/MM/YYYY'))"
                     rate
                     comment
                     user_id
                     isbn_no
                     (.format (new java.text.SimpleDateFormat "dd/MM/yyyy") (java.util.Date.))]))


(defn register-user!
  "Adds a new user to the databse and returns the user id"
  [db first-name last-name email password]
  (let [encrypted-pwd (hashers/derive password)]
    (:id (first (jdbc/query
                 db
                 ["INSERT INTO users (email, password, first_name, last_name)
                   VALUES (?, ?, ?, ?)
                   RETURNING id"
                  email
                  encrypted-pwd
                  first-name
                  last-name])))))

(defn login-user [db email]
  "Check if email and passwords match user credentials to login"
  (first (jdbc/query
            db
            ["SELECT id, first_name, password
                           FROM users
                           WHERE email = ?" email])))
