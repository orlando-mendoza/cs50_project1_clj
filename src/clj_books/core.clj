(ns clj-books.core
  (:use ring.middleware.flash)
  (:require [clj-books.mvc.handler :refer [handle-index
                                           handle-search-index
                                           handle-book-page
                                           handle-create-review
                                           handle-api-isbn
                                           login
                                           login-submit
                                           register
                                           register-submit
                                           logout]])
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
  (GET "/isbn/:isbn" [] handle-book-page)
  (POST "/isbn/:isbn" [] handle-create-review)
  (GET "/api/:isbn" [] handle-api-isbn)
  (GET "/login" [] login)
  (POST "/login" [] login-submit)
  (GET "/register" [] register)
  (POST "/register" [] register-submit)
  (GET "/logout" [] logout)

  (ANY "/request" [] handle-dump)
  (not-found "Page not found"))

(def app
  (wrap-server
   (wrap-reload
    (wrap-resource
     (wrap-db
      (wrap-params
       (wrap-session
        (wrap-flash
         routes) {:cookie-attrs {:max-age 3600}})))
     "static"))))


(defn -main [port]
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))

#_(defn -dev-main [port]
    (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))

(defonce server (jetty/run-jetty #'app {:port 8000 :join? false}))


;;(defonce server (run-server #'app {:port 5000 :join? false}))

(.start server)
(.stop server)

(selmer/cache-off!)

;; (clojure.pprint/print-table [{:a 1 :b 2 :c 3} {:a 4 :b 5 :c 6}])
