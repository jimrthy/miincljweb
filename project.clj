(defproject jimrthy/miincljweb "0.1.0-SNAPSHOT"
  :description "Skeleton for hanging a website on"
  :url "http://github.com/jimrthy/miincljweb"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.datomic/datomic-free "0.9.5350"]
                 [com.stuartsierra/component "0.3.1"]
                 [com.taoensso/timbre "4.3.1"]
                 ;; This depends on ring middleware.
                 ;; Which has weirdness without HttpServletRequest
                 [compojure "1.5.0"]

                 ;; TODO: Switch to enlive
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [http-kit "2.1.19"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.374"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [prismatic/schema "1.0.5"]
                 [ring/ring-defaults "0.2.0"]]
  :global-vars {*warn-on-reflection* true}
  :min-lein-version "2.0.0"
  :main miincljweb.core
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [org.clojure/java.classpath "0.2.3"]]}}
  :repl-options {:init-ns user
                 :timeout 120000
                 :welcome "Components workflow coming soon"})
