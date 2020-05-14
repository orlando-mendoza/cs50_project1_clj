-name: get-book-by-isbn
SELECT *
FROM books
WHERE isbn = :isbn

--name: new-user<!
INSERT INTO users (email, password, first_name, last_name) VALUES (:email, :password, :first-name, :last-name)

--name: control-user-by-mail
SELECT count(*) FROM users WHERE email = :email
