(ns relational.compositions
  (:require [relational.core :as c]))

(defn- create-or [args]
  (let [or (apply c/combine-partials-with (cons " OR " args))]
    (fn [db]
      (update-in (or db) [0] #(str "(" % ")")))))

(defrecord AndOr [composition attrs]
  c/IPartial
  (partial-fn [this]
    (case composition
      "OR" (create-or attrs)
      "AND" (apply c/combine-partials-with (cons " AND " attrs)))))

(defn compose [composition & attrs-with-nil]
  (let [attrs (remove nil? attrs-with-nil)]
    (when (> (count attrs) 0)
      (->AndOr composition attrs))))

(defrecord Not [rel]
  c/IPartial
  (partial-fn [this]
    (fn [db]
      (update-in ((c/partial-fn rel) db) [0] #(str "NOT(" % ")")))))
