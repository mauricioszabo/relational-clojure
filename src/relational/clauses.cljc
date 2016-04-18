(ns relational.clauses
  (:require [relational.core :refer [IPartial combine-partials-with]]
            [relational.selectables :as s]))


(defrecord Select [distinct? attributes]
  IPartial
  (partial-fn [_]
    (fn [db]
      (let [sel (apply combine-partials-with ", " attributes)
            sel-str (if distinct? "SELECT DISTINCT " "SELECT ")]
        (update-in (sel db) [0] #(str sel-str %))))))

(defn select [ & attributes]
  (->Select false (map s/literal attributes)))

(defn distinct [ & attributes]
  (->Select true (map s/literal attributes)))
