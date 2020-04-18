(defproject clj_books "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring "1.8.0"]
                 [compojure "1.6.1"]
                 [selmer "1.12.23"]
                 [refactor-nrepl "2.5.0"]
                 [aero "1.1.6"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [org.postgresql/postgresql "42.2.12.jre7"]
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.3"]]

  :main ^:skip-aot clj-books.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev     {:main clj-books.core/-dev-main}})
