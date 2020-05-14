(ns clj-books.mvc.services
  (:require [clojure.data.json :as json]
            [clj-http.client :as http]
            [clj-books.config :refer [config]]))

(defn get-good-reads-review [isbn]
  (let [api-key (get-in config [:secrets :api-key])]
    (-> (http/get
           "https://www.goodreads.com/book/review_counts.json"
           {:query-params {"key" api-key "isbns" isbn}})
        :body
        (json/read-str :key-fn keyword)
        :books
        first)))

(defn get-googleapi-book-info [isbn]
  (let [book-info (http/get (str "https://www.googleapis.com/books/v1/volumes?q=isbn:" isbn ))]
    (-> book-info
        :body
        (json/read-str :key-fn keyword)
        :items
        first)))

(defn get-book-cover [isbn]
  (let [book-info (get-googleapi-book-info isbn)]
    (get-in book-info [:volumeInfo :imageLinks :thumbnail] "Not Found")))
