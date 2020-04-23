(ns clj-books.mvc.handler
  (:require [clj-books.mvc.model :as model]
            [selmer.parser :as selmer]
            [clj-books.config :refer [db-spec]]
            [clojure.java.jdbc :as jdbc]
            [ring.util.response :as response])
  (:gen-class))

(defn handle-index [req]
  (let [db (:clj-books/db req)
        books (model/select-books db)
        username (get-in req [:session :username])]
    (selmer/render-file "templates/index.html" {:current-user username :books books})))

(defn handle-search-index [req]
  (let [username (get-in req [:session :username])
        db (:clj-books/db req)
        search-by (get-in req [:form-params "search-by"])
        search-text (get-in req [:form-params "search-text"])
        books (model/select-books db search-by search-text)]
    (selmer/render-file "templates/index.html" {:current-user username :books books})))

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
  (selmer/render-file "login.html" {:title "Login"}))

(defn register [req]
            ;; check if the user is already logged in and redirects it to index page
            (if (not (nil? (get-in req [:session :username ])))
              (response/redirect "index.html" )

              ;; if the user is not logged in then proceeds to validate and register
              (do
                (case (:request-method req)
                  :GET
                  (selmer/render-file "templates/register.html" {:title "Register"})
                  :POST
                  (do
                    (let [params (get-in (:params req))
                          {:keys [first-name
                                  last-name
                                  email
                                  password
                                  confirm-password]} params]
                      ((if not = (password confirm-password))
                       (selmer/render-file "register.html" {:title "Register"
                                                            :messages ["Password entered doesn't match", "danger"] })

                       ;; if passwords do match then register the user
                       (let [db (:webdev/db req)]
                         (if (> (model/control-user-by-email {:email email}) 0)
                           (selmer/render-file "register.html" {:title "Register"
                                                                :messages ["Email already registered, please try again", "danger"]})
                           (:else
                            (do
                              (let [user-id (model/new-user<! {:first-name first-name
                                                               :last-name  last-name
                                                               :email      email
                                                               :password   password})]
                                (-> (response/redirect "index.html" {:current-user first-name
                                                                     :message ["You have successfuly registered, now you are logged in!", "success"]})
                                    (assoc :session {:current-user first-name
                                                     :user-id user-id})))))))))))))
            )
