(ns miincljweb.renderer
  (:gen-class)
  (:require [[clostache.parser :as clostache]]))

(defn read-template [template-name]
  "Pull a template file off the disk.
For anything real, this should definitely be memoized."
  (try
    (let* [file-name (str "templates/" template-name ".mustache")
           template 
           (slurp (clojure.java.io/resource file-name))]
          template)
    (catch Exception e
      (throw (Exception. (str "Template Read Error: " e) e)))))

(defn render-template [template-file params]
  "Merge params into a file specified by template-file.
Note that template-file is actually the name of a file (no extension: I'm
hard-coding that to mustache) in a templates folder resource on the classpath"
  (try
    (clostache/render (read-template template-file) params)
    (catch Exception e
      (str "Template Render Error: " e))))

