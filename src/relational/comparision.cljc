(ns relational.comparision
  (:require [clojure.string :refer [replace]]
            [relational.core :as c]))

;;   "Escapes attributes and table names.

;; For now, SQL-standard tables are escaped with \", and
;; MySQL is escaped with `")

; TODO: Check this escapes, please!
(defmulti escape-attr-name (fn [db _] (:adapter db)))
(defmethod escape-attr-name :mysql [db name]
  (str "`" name "`"))
(defmethod escape-attr-name :default [db name]
  (str \" (replace name #"\"" "\"\"") \"))

(defrecord Table [name]
  c/IPartial
  (partial-fn [this] (fn [db] [(escape-attr-name db (:name this)) nil])))

(defrecord Attribute [table name]
  c/IPartial
  (partial-fn [this]
    (let [attr (->Table (:name this))] ; don't care, it's the same escape
      (c/combine-partials-with "." table attr))))

(defn =
  ([a1 a2])
  ([a1 a2 & rest]))
