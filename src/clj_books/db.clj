(ns clj-books.db
  (:require [clojure.java.jdbc :as db]
            [yesql.core :refer [defqueries]]))

;; TODO: remove config and db-conn once you've done a wrap-db
(def config
  (aero.core/read-config (clojure.java.io/resource "clj_books/config.edn")))

(defn set-db-conn [{:keys [dbtype dbname host user password]}]
  (let [db-conn {:dbtype   dbtype
                 :dbname   dbname
                 :host     host
                 :user     user
                 :password password}]
    db-conn))

(def db-spec (set-db-conn config))

(defqueries "clj-books/sql.slq" {:connection db-spec})
