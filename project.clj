(defproject clj_books "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[aero "1.1.6"]
                 [buddy/buddy-hashers "1.4.0"]
                 [clj-http "3.10.1"]
                 [com.novemberain/validateur "2.6.0"]
                 [compojure "1.6.1"]
                 [noir "1.3.0"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [org.postgresql/postgresql "42.2.12.jre7"]
                 [refactor-nrepl "2.5.0"]
                 [ring "1.8.0"]
                 [ring/ring-anti-forgery "1.3.0"]
                 [selmer "1.12.23"]]

  :main ^:skip-aot clj-books.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :ring {:open-browser? false
         :stacktraces? false
         :auto-reload? true})
