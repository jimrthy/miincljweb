(defproject miincljweb "0.1.0-SNAPSHOT"
  :description "Skeleton for hanging a website on"
  :url "http://github.com/jimrthy/miincljweb"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 ;; Very tempting to replace the next line, considering the problems
                 ;; I'm having with clostache at work. Leave it be for now, but definitely
                 ;; think about it.
                 [de.ubercode.clostache/clostache "1.3.1"]
                 [korma "0.3.0-RC5"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [http-kit "2.1.3"]
                 [org.clojure/tools.nrepl "0.2.3"]]
  ;; I'm pretty sure this next line is obsolete
  ;; (I should be using profiles instead
  :plugins [[lein-ring "0.8.5"]]
  :ring {:handler miincljweb.core/app}
  :min-lein-version "2.0.0"
  :main miincljweb.core)
