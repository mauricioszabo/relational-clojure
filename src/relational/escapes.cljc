(ns relational.escapes
  (:require [clojure.string :refer [replace]]))

(defmulti escape-attr-name (fn [db _] (:adapter db)))

(defmethod escape-attr-name :mysql [db name]
  (str "`"
       (replace name #"`" "``")
       "`"))

(defmethod escape-attr-name :default [db name]
  (str \"
       (replace name #"\"" "\"\"")
       \"))

