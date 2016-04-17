(ns relational.compositions
  (:require [relational.core :as c]))

(defn- create-or [args]
  (let [or (apply c/combine-partials-with (cons " OR " args))]
    (fn [db]
      (update-in (or db) [0] #(str "(" % ")")))))

(defn compose [composition first & rest]
  (case composition
    "OR" (reify c/IPartial
           (partial-fn [this] (create-or (cons first rest))))
    "AND" (reify c/IPartial
            (partial-fn [this]
              (apply c/combine-partials-with (cons " AND " (cons first rest)))))))

(defn not [rel]
  (reify c/IPartial
    (partial-fn [this]
      (fn [db]
        (update-in ((c/partial-fn rel) db)
                   [0]
                   #(str "NOT(" % ")"))))))
