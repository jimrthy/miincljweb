(defproject jimrthy/miincljweb "0.1.0-SNAPSHOT"
  :description "Skeleton for hanging a website on"
  :url "http://github.com/jimrthy/miincljweb"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.datomic/datomic-free "0.9.5350" :exclusions [commons-codec]]
                 [com.stuartsierra/component "0.3.1"]
                 [com.taoensso/timbre "4.3.1" :exclusions [org.clojure/tools.reader]]
                 ;; This depends on ring middleware.
                 ;; Which has weirdness without HttpServletRequest
                 [compojure "1.5.0" :exclusions [org.clojure/tools.reader]]

                 ;; TODO: Switch to enlive
                 ;; Actually...let individual modules cope with this sort of thing
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [http-kit "2.1.19"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.374"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [prismatic/schema "1.0.5"]
                 [ring/ring-defaults "0.2.0"]]
  :global-vars {*warn-on-reflection* true}
  :min-lein-version "2.0.0"
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [org.clojure/java.classpath "0.2.3"]]}}
  :repl-options {:init-ns user
                 :timeout 120000
                 :welcome "Use (dev) then (reset) to start"})
