(ns demo.action
  (:use [utils]
        [demo.db-config]
        [clojure.set]
        [clj-time.core  :exclude (extend)])
  (:require [demo.db :as db]
            [clojure.string :only (capitalize replace-first upper-case) :as st]))

(def beginning-time)
(def op)
(def cr)
(def rt)
(def ord)
(def grp)
(def mf)
(def exc)
(def ch-op)
(def ch-grp)
(def ch-ord)
(def ch-cr)
(def mem-mata-fields nil)
(def err "Invalid Report")
(def err_grp "Please select only one Group Function column.")

(defn filter-req
  [prefix req-map]
  (into {} (filter #(.startsWith (name (key %)) prefix) req-map)))

(defn remove-db-prefix
  [kee prefix]
  (st/replace-first (name kee) (str prefix ".") ""))

(defn filter-list-by-prefix
  "Return list of filtered request with prefix"
  [prefix req-map]
  (let [mp (filter-req prefix req-map)]
    (map #(remove-db-prefix % prefix) (keys mp))))

(defn filter-map-by-prefix
  "Return map of filtered request with prefix"
  [prefix req-map]
  (let [crmap (filter-req prefix req-map)]
    (zipmap (map #(keyword (remove-db-prefix % prefix))
                 (keys crmap)) (vals crmap))))

(defn mf-to-clm
  [mf clmval]
  (let [kee (keyword clmval)]
    (if (contains? mf kee) (get mf kee) clmval)))

(defn rpl-clms
  [mf clms]
  (when-not (and (if-nil-or-empty mf) (if-nil-or-empty clms))
    (map #(mf-to-clm mf %) clms)))

(defn change-params
  [mf op grp ord cr]
  (def ch-op (rpl-clms mf op))
  (def ch-ord (rpl-clms mf ord))
  (def ch-grp (zipmap (rpl-clms mf (keys grp)) (vals grp)))
  (def ch-cr (zipmap (rpl-clms mf (keys cr)) (vals cr))))

(defn filter-RT
  [rtval]
  (first (filter #(= (st/upper-case rtval) (st/upper-case %)) 
                 (keys db/cached-schema))))

(defn get-mf
  [pmf]
  (zipmap (map keyword pmf) 
          (map #(str meta_alias (.indexOf pmf %) "." (:COLUMN metadata-value)) pmf)))

(defn deselection
  [mp ls]
  (let [kees (set (keys mp))]
    (apply dissoc mp 
           (difference kees (intersection (set (map keyword ls)) kees)))))

(defn create-query-seqs
  [req-map]
  (def exc (filter-list-by-prefix EXC req-map))
  (let [pout (filter-list-by-prefix CLM req-map)
        pmf (filter-list-by-prefix MTA req-map)
        pcr (filter-map-by-prefix TXT req-map)
        pgrp (filter-map-by-prefix GRP req-map)
        pord (filter-list-by-prefix ORD req-map)]
    (def op (remove (fn [i] (some #(= i %) exc)) pout))
    (def ord (intersection (set pord) (set pout)))
    (def cr (deselection pcr pout))
    (def grp (deselection pgrp pout))
    (def mf (deselection (get-mf pmf) pout)))
  (def rt (filter-RT (str prf ((keyword RT) req-map))))
  (def ch-op op)(def ch-cr cr)(def ch-ord ord)(def ch-grp grp)
  (let [pext ((keyword EXT) req-map)]
    (when-not (if-nil-or-empty pext)
      (def ch-op (conj ch-op pext))))
  (when-not (if-nil-or-empty mf) 
    (change-params mf op grp ord cr)))

(defn get-result
  []
  (when-not (utils/if-nil-or-empty op)
    (if (> (count grp) 1)
      {:Error err_grp}   
      (try
        (db/execute-query 
          (db/create-query-str op ch-op cr ch-cr rt ord ch-ord 
                               (first grp) (first ch-grp) mf))
        (catch Exception _ {:Error err})))))

(defn get-schema
  []
  (let [beginning-time (java.util.Date.)]
    (db/set-util-prf)
    (db/fetch-db-table-columns-map)
    (db/get-pk-ralation db/cached-schema)
    (db/create-db-graph)
    (println "Initialization complete :)") 
    (println "Time taken:" (timeTaken beginning-time) "mins")
    db/cached-schema))

(defn create-headers
  [prefix replaceby coll]
  (map #(-> (st/replace-first (st/upper-case %) prefix replaceby)
          (st/capitalize)) coll))

(defn get-header-clms
  []
  (create-headers (val-up prf) "" op))     