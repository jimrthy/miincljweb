(ns com.jimrthy.cluster-web.web.renderer
  (:require [net.cgrand.enlive-html :as html]))

(html/deftemplate index "public/index.html"
  [greeting]
  [:h1] (html/content (str (:greeting greeting) ", World")))
