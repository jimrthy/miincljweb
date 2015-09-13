(defproject jimrthy/miincljweb "0.1.0-SNAPSHOT"
  :description "Skeleton for hanging a website on"
  :url "http://github.com/jimrthy/miincljweb"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.taoensso/timbre "3.4.0"]
                 ;; This depends on ring middleware.
                 ;; Which has weirdness without HttpServletRequest
                 [compojure "1.4.0"]
                 ;; TODO: Switch to enlive
                 [de.ubercode.clostache/clostache "1.3.1"]
                 [http-kit "2.1.19"]
                 ;; Q: What was I doing with this?
                 ;; And can it go away?
                 [korma "0.4.2"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 ;; TODO: Use a real database
                 ;; Actually, should look into options for
                 ;; handling multiple databases.
                 ;; Q: Is there any good way to do that without
                 ;; downloading a ton of needless dependencies?
                 ;; A: That really seems to be what profiles are for.
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [ring/ring-defaults "0.1.5"]]
  :global-vars {*warn-on-reflection* true}
  :min-lein-version "2.0.0"
  :main miincljweb.core
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [org.clojure/java.classpath "0.2.2"]]}}
  :repl-options {:init-ns user})
