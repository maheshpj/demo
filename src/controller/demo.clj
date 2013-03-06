(ns controller.demo
  (:use [compojure.core :only (defroutes GET POST)]
        [ring.adapter.jetty :only (run-jetty)]
        [hiccup.page :only (html5)]
        [clojure.string :only (replace-first)])
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
  filter-req
  [prefix req-map]
  (into {}
    (filter 
      #(.startsWith (name (key %)) prefix) 
      req-map)))

(defn
  remove-db-prefix
  [kee prefix]
  (replace-first 
       (name kee) (str prefix ".") ""))
(defn
  filter-list-by-prefix
  "Return list of filtered request with prefix"
  [prefix req-map]
  (map 
    #(remove-db-prefix (key %) prefix)
    (filter-req prefix req-map)))

(defn
  filter-map-by-prefix
  "Return map of filtered request with prefix"
  [prefix req-map]
  (let [crmap (filter-req prefix req-map)]
    (zipmap 
      (map #(keyword 
              (remove-db-prefix % prefix))
           (keys crmap))
      (vals crmap))))

(defn
  process-request
  [req-map]
  (index 
    (reverse (filter-list-by-prefix "CLM" req-map))
    (filter-map-by-prefix "TXT" req-map)))

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