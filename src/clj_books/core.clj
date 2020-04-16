(ns clj-books.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]])
  (:gen-class))

(defn greet [req]
  {:status 200 :body "Olha a cara do bis!" :headers {}})

(defn goodbye [req]
  {:status 200
   :body "Goodbye, Cruel World!"
   :headers {}})

(defn- yo-body
  [name]
  {:status 200
        :body (str "Yo! " name)
        :headers {}})

(defn yo [req]
  (let [name (get-in req [:route-params :name])]
    (yo-body name)))

(defn isbn [req]
  (let [isbn (get-in req [:route-params :isbn])]
    ))

(defroutes app
  (GET "/" [] greet)
  (GET "/goodbye" [] goodbye)
  (GET "/yo/:name" [] yo)
  (GET "/request" [] handle-dump)
  (GET "/pijibaye" [] {:status 200 :body "Pijibaye Ridista" :headers {}})
  (not-found "Page not found"))

(defn -main [port]
  (jetty/run-jetty app {:port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
