(ns relational.core
  (:require [clojure.string :as str]))

(defmacro db-for [ db-name & {:keys [adapter] :as params}]
  `(def ~db-name ~params))

(defprotocol IPartial
  (partial-fn [this]
              (str "Returns a function that, given a database config, "
                   "returns a vector with a SQL fragment and his attributes")))

(defn- reduce-partial [join-str [first-sql first-attrs] [second-sql second-attrs]]
  [(str first-sql join-str second-sql), (vec (concat first-attrs second-attrs))])

(defn combine-partials-with [join-str partials]
  (fn [db]
    (let [[first-partial & partials] partials
          first-partial-fn (partial-fn first-partial)
          seed (first-partial-fn db)]
      (->> partials
           (map #((if (fn? %) % (partial-fn %)) db))
           (reduce (partial reduce-partial join-str) seed)))))


(defn- reduce-attr [sql attr]
  (let [string (cond
                 (number? attr) (str attr)
                 (string? attr) (str "'" (str/replace attr #"'" "''") "'")
                 :else (str "\"" attr "\""))]
    (str/replace-first sql #"\?" string)))

(defn to-pseudo-sql [partial db]
  (let [partial-fn (partial-fn partial)
        [sql-fragment attrs] (partial-fn db)]
    (reduce reduce-attr sql-fragment attrs)))
