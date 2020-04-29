(ns clj-books.config
  (:require  [aero.core :refer [read-config]]
             [clojure.java.io :as io]
             [selmer.parser :refer [set-resource-path!
                                    add-tag!]])
  (:use ring.util.anti-forgery)
  (:gen-class))

(def config
  (read-config (io/resource "clj_books/config.edn")))

(def db-spec (get-in config [:secrets :database-spec]))


;; set parser resource path
;;(set-resource-path! (io/resource "resource"))
;; (-> req :route-params :name) - a way to get the name from route-params in request

;; creates the session anti-forgery token
(add-tag! :csrf-token (fn [args ctc-map] (anti-forgery-field)))
                                        ;(filters/add-filter! :empty? empty?)
