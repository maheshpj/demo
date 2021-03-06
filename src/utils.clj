(ns utils
  (:use [clojure.contrib.math])
  (:require [clojure.string :only (trim upper-case replace blank?) :as st]))

(def prf "")
(def CLM "CLM")
(def TXT "TXT")
(def ORD "ORD")
(def DIV "DIV")
(def HDN "HDN")
(def RT "RT")
(def GRP "GRP")
(def MTA "MTA")
(def EXC "EXC")
(def EXT "EXT")
(def group-fun (list "AVG" "COUNT" "DISTINCT" "MAX" "MIN" "SUM"))
(def db-grph-keys [:fkcolumn_name :pktable_name :fktable_name])
(def uppercase " UPPER")

(defn convert-form-string-to-map
  "converting request string '1=1&2=2&3=3' into map {:1 1, :2 2, :3 3}"
  [form-str]
  (reduce 
    #(assoc % (keyword (read-string (nth %2 1))) (nth %2 2)) {} 
      #_> (re-seq #"([^=&]+)=([^=&]+)" form-str)))

(defn map-tag 
  [tag style-map xs]
  (map (fn [x] [tag style-map x]) xs))

(defn if-nil-or-empty
  [any]
  (or (nil? any) (empty? any)))

(defn val-up
  [vl]
  (st/upper-case (st/trim vl)))

(defn set-prf
  [pprf]
  (if (nil? pprf) (def prf "") (def prf pprf)))

(defn parenthise
  [vl]
  (str "(" vl ")"))

(defn clm-up
  [vl]
  (str uppercase (parenthise (st/trim vl))))

(defn round-places [number decimals]
  (let [factor (expt 10 decimals)]
    (bigdec (/ (round (* factor number)) factor))))

(defn timeTaken
  [start]
  (round-places (double (/ (- (.getTime (java.util.Date.)) 
                              (.getTime start)) 
                           60000)) 2))