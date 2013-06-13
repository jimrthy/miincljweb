(ns miincljweb.system)

;;;; Based on Stuart Sierra's workflow (thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded)
;;;; Probably stupid and pointless to include this here, but at least it's some sort of skeletal
;;;; reminder about how to do things.

(defn system
  "Returns a new instance of the application state.
Things like defining database connections and a placeholder for a web server.
No side effects. Totally different than actually starting the system."
  []
  (throw (RuntimeException. "Implement this")))

(defn start
  "Performs side-effects to initialize system, acquire resources, and start it running.
Returns an updated instance of the system.
Dangerous: if this throws an exception, it could easily lock a resource with no way to
release. Pretty much the only way out then is to restart the JVM."
  [system]
  system)

(defn stop
  "Performs side-effects to stop system and release its resources.
Returns an updated instance of the system.
Dangerous in pretty much exactly the same way as start."
  [system]
  system)
