-name: get-book-by-isbn
SELECT *
FROM books
WHERE isbn = :isbn

--name: new-user<!
INSERT INTO users (email, password, first_name, last_name) VALUES (:email, :password, :first-name, :last-name)

--name: check-if-mail-exists
SELECT count(*) FROM users WHERE email = :email
