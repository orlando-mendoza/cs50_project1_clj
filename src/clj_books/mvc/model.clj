(ns clj-books.mvc.model
  (:require [clojure.java.jdbc :as jdbc]
            [buddy.hashers :as hashers])
  (:gen-class))

(defn control-user-by-email
  "Check if under registration the email already exists in the database returning true for success and false for failure"
  [db email]
  (not= 0 (apply int (map :count (jdbc/query db ["SELECT count(*) FROM users WHERE email = ?" email])))))

(def db {:dbtype "postgresql"
         :dbname "d9thck2in9fq7f"
         :host "ec2-174-129-242-183.compute-1.amazonaws.com"
         :user "cyvqdelufebjjr"
         :password "21d0a5ae342e99c3f83192caf54e440c7ae4b3456fc9fe54f22521113be4df60"})
(use 'clojure.test)

(is (not= 0 (apply int (map :count (jdbc/query db ["select count(*) from users where email = ?" "newradicals@90s.com"])))))

(control-user-by-email db "pijebaye@gmail.com")

(defn select-books
  "returns the list of books according to the params"
  ([db]
   (jdbc/query db ["SELECT * FROM books"]))
  ([db search-by search-text]
   (jdbc/query db [(str "SELECT * FROM books WHERE " search-by " iLIKE '%" search-text "%'")])))

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
