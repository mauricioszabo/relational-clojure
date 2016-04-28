(ns relational.clauses
  (:require [relational.core :refer [IPartial combine-partials-with partial-fn]]
            [relational.selectables :as s]))

(defn- add-prefix [prefix partial]
  (fn [db]
    (update-in (partial db) [0] #(str prefix %))))

(defn- add-prefix-to-list [prefix list]
  (add-prefix prefix (apply combine-partials-with ", " list)))

(defrecord Select [distinct? attributes]
  IPartial
  (partial-fn [_]
    (if distinct?
      (add-prefix-to-list "SELECT DISTINCT " attributes)
      (add-prefix-to-list "SELECT " attributes))))

(defn select [ & attributes]
  (if (empty? attributes)
    (->Select false [s/all])
    (->Select false (map s/literal attributes))))

(defn distinct [ & attributes]
  (if (empty? attributes)
    (->Select true [s/all])
    (->Select true (map s/literal attributes))))

(defrecord EmptyClause []
  IPartial
  (partial-fn [_]
    (fn [_] ["" nil])))
(def ^{:private true} empty-clause (->EmptyClause))

(defrecord From [table-likes]
  IPartial
  (partial-fn [_]
    (add-prefix-to-list "FROM " table-likes)))

(defn from [ & table-likes]
  (if (empty? table-likes) empty-clause (->From table-likes)))

(defrecord Comparision [clause comparision]
  IPartial
  (partial-fn [_]
    (add-prefix (str clause " ") (partial-fn comparision))))

(defn where [comparision]
  (if (nil? comparision) empty-clause (->Comparision "WHERE" comparision)))

(defn having [comparision]
  (if (nil? comparision) empty-clause (->Comparision "HAVING" comparision)))

(defrecord GroupBy [partials]
  IPartial
  (partial-fn [_]
    (add-prefix-to-list "GROUP BY " partials)))

(defn group-by [ & partials]
  (if (empty? partials) empty-clause (->GroupBy partials)))
