(ns clj-books.handler
  (:require [selmer.parser :as selmer]
            [clj-books.config :refer [db-spec]]
            [clojure.java.jdbc :as jdbc]))


(defn handle-index [req]
  (let [books (jdbc/query db-spec ["SELECT * FROM books"])
        username (get-in req [:session :username])]
    (selmer/render-file "templates/index.html" {:current-user username :books books})))

(defn handle-search-index [req]
  ;; TODO
  {:status 200
   :headers {}
   :body ""}
  )

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
        (selmer/render-file "register.html" {:title "Register"})
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
             (:else
              (do
                (if (> (db/control-user-by-email {:email email}) 0)
                  (selmer/render-file "register.html" {:title "Register"
                                                       :messages {["Email already registered, please try again", "danger"]}})
                  (:else
                   (do
                     (let user-id (db/new-user<! {:first-name first-name
                                                  :last-name  last-name
                                                  :email      email
                                                  :password   password}) :id)
                     (-> (response/redirect "index.html" {:current-user first-name
                                                          :message {["You have successfuly registered, now you are logged in!", "success"]}})
                         (assoc :session {:current-user first-name
                                          :user-id user-id}))))))))))))))
