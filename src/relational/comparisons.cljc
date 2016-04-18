(ns relational.comparisons
  (:require [relational.core :refer [IPartial combine-partials-with partial-fn] :as c]
            [relational.selectables :refer [literal]]
            [relational.compositions :refer [compose]]
            [clojure.string :refer [join]]))

(defrecord Comparison [comparison a1 a2]
  IPartial
  (partial-fn [this]
    (combine-partials-with (str " " comparison " ") (literal a1) (literal a2))))

(defn comparison [comparison & attributes]
  (let [comb (str " " comparison " ")
        pairs (->> attributes
                   (partition 2 1)
                   (map #(apply ->Comparison comparison %)))]
    (apply compose "AND" pairs)))

(defrecord Is [attribute what]
  IPartial
  (partial-fn [this]
    (fn [db]
      (update-in ((partial-fn attribute) db) [0] str " IS " what))))

(defn is-null [attr] (->Is attr "NULL"))
(defn is-not-null [attr] (->Is attr "NOT NULL"))

(defrecord InSeq [attribute sequence]
  IPartial
  (partial-fn [this]
    (let [seq-str (str "(" (join "," (repeat (count sequence) "?")) ")")
          f (fn [db] [seq-str sequence])]
      (combine-partials-with " IN " attribute f))))

(defn in [attribute seq-or-sql]
  (->InSeq attribute seq-or-sql))

