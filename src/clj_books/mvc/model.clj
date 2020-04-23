(ns clj-books.mvc.model
  (:require [clojure.java.jdbc :as db])
  (:gen-class))

(defn control-user-by-email
  "Check if under registration the email already exists in the database returning true for success and false for failure"
  [db email]
  (= [1] (db/execute! db ["SELECT email FROM users WHERE email = ?" email])))

(defn select-books
  "returns the list of books according to the params"
  ([db search-by search-text]
   (db/query db [(str "SELECT * FROM books WHERE " search-by " iLIKE '%" search-text "%'")]))
  ([db]
   (db/query db ["SELECT * FROM books"])))
