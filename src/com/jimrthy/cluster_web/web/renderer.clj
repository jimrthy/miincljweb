(ns miincljweb.renderer
  (:require [clostache.parser :as clostache]))

(defn read-template
  "Pull a template file off the disk.
  For anything real, this should definitely be memoized."
  [template-name]
  (try
    (let* [file-name (str "templates/" template-name ".mustache")
           template
           (slurp (clojure.java.io/resource file-name))]
          template)
    (catch Exception e
      (throw (Exception. (str "Template Read Error: " e) e)))))

(defn render-template
  "Merge params into a file specified by template-file.
  Note that template-file is actually the name of a file (no extension: I'm
  hard-coding that to mustache) in a templates folder resource on the classpath"
  [template-file params]
  (try
    (clostache/render (read-template template-file) params)
    (catch Exception e
      (str "Template Render Error: " e))))
