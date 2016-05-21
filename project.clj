(defproject com.jimrthy/cluster-web "0.1.0-SNAPSHOT"
  :description "Skeleton for hanging a website on"
  :url "http://github.com/jimrthy/miincljweb"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[bidi "2.0.9"]
                 ;; Q: Does this make any sense in here?
                 ;; A: Well...part of the point is that this should be opinionated
                 [com.datomic/datomic-free "0.9.5359" :exclusions [commons-codec]]
                 [com.stuartsierra/component "0.3.1"]
                 [com.taoensso/timbre "4.3.1" :exclusions [org.clojure/tools.reader]]
                 ;; I really don't want to force a templating engine on anyone else.
                 ;; Especially since they're so 2015.
                 ;; But I need one for the way the current examples are written
                 [enlive "1.1.6"]
                 [http-kit "2.1.19"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.374" :exclusions [org.clojure/tools.reader]]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [org.clojure/tools.reader "1.0.0-alpha1"]
                 [prismatic/schema "1.1.1"]
                 [ring/ring-defaults "0.2.0"]]
  :global-vars {*warn-on-reflection* true}
  :jvm-opts ["-Djava.awt.headless=true"]
  :min-lein-version "2.0.0"
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [org.clojure/java.classpath "0.2.3"]]}}
  :repl-options {:init-ns user
                 :timeout 120000
                 :welcome "Use (dev) then (reset) to start"})
