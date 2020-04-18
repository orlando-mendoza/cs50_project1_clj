(ns clj-books.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [aero.core :as aero]
            [clojure.java.io :as io]
            [selmer.parser :as selmer])
  (:gen-class))

(def config
  (aero/read-config (io/resource "clj_books/config.edn")))

(def db-conn (:database-url config))

(selmer/set-resource-path! (io/resource "templates"))
;; (selmer.parser/set-resource-path! nil)
;; (-> req :route-params :name) - a way to get the name from route-params in request


(selmer/render-file "base.html" {:current-user "omendozar" :messages nil})

(defn isbn [req]
  (let [isbn (get-in req [:route-params :isbn])]
    ))

(defroutes routes
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
