(defproject Demo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/data.json "0.2.1"]
                 [org.clojure/clojure-contrib "1.2"]
                 [ring/ring-jetty-adapter "1.0.0-RC1" :exclusions [org.clojure/clojure
                                                                   org.clojure/clojure-contrib]]
                 [compojure "0.6.5" :exclusions [org.clojure/clojure]]
                 [hiccup "1.0.2" :exclusions [org.clojure/clojure]]
                 [log4j "1.2.16" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]
                 [jline "0.9.94"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [org.clojure/java.jdbc.sql "0.2.3"]
                 [com.oracle/ojdbc6 "11.2.0.3"]
                 [postgresql "9.0-801.jdbc4"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [jkkramer/loom "0.2.0"]]
  :repositories {"codelds" "https://code.lds.org/nexus/content/groups/main-repo"})
