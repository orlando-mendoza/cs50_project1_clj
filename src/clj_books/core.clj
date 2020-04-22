(ns clj-books.core
  (:require [clj-books.handler :refer [handle-index
                                       handle-search-index
                                       handle-isbn-details
                                       handle-create-review
                                       handle-api-isbn
                                       login
                                       register]])
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [defroutes GET POST ANY]]
            [compojure.route :refer [not-found]]
            [selmer.parser :as selmer])
  (:use [clj-books.config])
  (:gen-class))

(defn wrap-db
  "wraps the db-spec connection so every request will hace access
  to the database information at clj-books.db"
  [hdlr]
  (fn [req]
    (hdlr (assoc req :clj-books/db db-spec))))

(defn wrap-server [hdlr]
  (fn [req]
    (assoc-in (hdlr req) [:headers "Server"] "Books Reviews")))

(defroutes routes
  (GET "/" [] handle-index)
  (POST "/" [] handle-search-index)
  (GET "/isbn/:isbn" [] handle-isbn-details)
  (POST "/isbn/:isbn" [] handle-create-review)
  (GET "/api/:isbn" [] handle-api-isbn)
  (GET "/login" [] login)
  (GET "/register" [] register)

  (ANY "/request" [] handle-dump)
  (not-found "Page not found"))

(def app
  (wrap-server
   (wrap-reload
    (wrap-resource
     (wrap-db
      (wrap-params
       (wrap-session
        routes {:cookie-attrs {:max-age 3600}})))
     "static"))))


(defn -main [port]
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))

#_(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))

(defonce server (jetty/run-jetty #'app {:port 8000 :join? false}))


;;(defonce server (run-server #'app {:port 5000 :join? false}))

(.start server)
(.stop server)
