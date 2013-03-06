(ns controller.demo
  (:use [compojure.core :only (defroutes GET POST)]
        [ring.adapter.jetty :only (run-jetty)]
        [hiccup.page :only (html5)])
  (:require [compojure.route :as route]
            [views.index :as idx]
            [utils]
            [ring.util.response :as ring]))

(defn 
  index 
  [output-list criteria-map]
  (html5
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1"}]
     [:title "AutoQuery Demo"]]
    [:body (idx/schema-form output-list criteria-map)]))

(defn
  filter-by-prefix
  "Return map of filtered request with prefix"
  [prefix req-map]
  (map 
    #(clojure.string/replace-first 
       (name (key %)) 
       (str prefix ".") 
       "")
    (filter 
      #(.startsWith 
         (name (key %)) 
         prefix) 
      req-map)))

(defn
  get-selected-clms
  [coll]
  (reverse 
      (map name (keys coll))))

(defn
  process-request
  [req-map]
  (index 
    (get-selected-clms 
      (filter-by-prefix "CLM" req-map))
    (filter-by-prefix "TXT" req-map)))

(defn
  run
  [req]
  (when-not (utils/if-nil-or-empty req)
    (process-request 
      (utils/convert-form-string-to-map
        (slurp (req :body))))))

(defroutes 
  routes
  (GET "/" [] (index '() '()))
  (POST "/run" request (run request)))