(ns clj-books.validators.user-validator
  (:require [validateur.validation :refer :all]
            [noir.validation :as v]))

(def email-validator
  (validation-set
   (validate-with-predicate :email
                            #(v/is-email? (:email %))
                            :message-fn (fn [validation-map]
                                          (if (v/has-value? (:email validation-map))
                                            "The email's format is incorrect"
                                            "is a required field")))))

(def password-validator
  (validation-set
   (length-of :password
              :within (range 7 101)
              :blank-message "is a required field"
              :message-fn (fn [type m attribute & args]
                            (if (= type :blank)
                              "is a required field"
                              "Passwords must be between 7 and 100 characters long.")))))

(defn validate-signup [signup]
  "Validates the incoming signup map and returns a
   set of error messages for any invalid field
   Expects signup to have: :email, and :password."
  ((compose-sets email-validator password-validator) signup))

(validate-signup {:email "pijibaye@dot.com"})
;; => {:password #{"is a required field"}}
;; => {:password #{"can't be blank"}}
(validate-signup {:email "pijibaye@dot.com" :password "123456"})

(validate-signup {:username "El bis" :email "el.bis@dot.com" :password "1234"})
;; => {:username #{"Only letters, numbers, dots and underscores alowed"}}
(validate-signup {:username "omendozar" :email "el.bis@dot.@com" :password "1234567"})
;; => {:email #{"the email's format is incorrect"}}
