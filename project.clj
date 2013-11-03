(defproject miincljweb "0.1.0-SNAPSHOT"
  :description "Skeleton for hanging a website on"
  :url "http://github.com/jimrthy/miincljweb"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.taoensso/timbre "2.6.3"]
                 [compojure "1.1.5"]
                 ;; TODO: Switch to enlive
                 [de.ubercode.clostache/clostache "1.3.1"]
                 [http-kit "2.1.3"]
                 [korma "0.3.0-RC5"]
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.nrepl "0.2.3"]
                 ;; TODO: Use a real database
                 ;; Actually, should look into options for
                 ;; handling multiple databases.
                 ;; Q: Is there any good way to do that without
                 ;; downloading a ton of needless dependencies?
                 ;; A: That really seems to be what profiles are for.
                 [org.xerial/sqlite-jdbc "3.7.2"]]
  :global-vars {*warn-on-reflection* true}
  :min-lein-version "2.0.0"
  :main miincljweb.core
  ;; I'm pretty sure this next line is obsolete
  ;; (I should be using profiles instead
  ;; Especially since I've switched to http-kit...
  ;; That seems like a very debatable choice.
  :plugins [[lein-ring "0.8.5"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]
                                  [org.clojure/java.classpath "0.2.0"]]}}
  :repl-options {:init-ns user})
