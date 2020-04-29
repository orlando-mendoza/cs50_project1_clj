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
              :within (range 3 101)
              :blank-message "is a required field"
              :message-fn (fn [type m attribute & args]
                            (if (= type :blank)
                              "is a required field"
                              "Passwords must be between 3 and 100 characters long.")))))

(def confirm-password-validator
  (validation-set
   (validate-by :confirm-password :password :message "Confirm password doesn't match")))

(defn validate-signup [signup]
  "Validates the incoming signup map and returns a
   set of error messages for any invalid field
   Expects signup to have: :email, and :password."
  ((compose-sets email-validator password-validator confirm-password-validator) signup))
