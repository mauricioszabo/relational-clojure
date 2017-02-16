(ns relational.escapes
  (:require [clojure.string :as str]))

(defmulti escape-attr-name (fn [db _] (:adapter db)))

(defmethod escape-attr-name :mysql [db name]
  (str "`"
       (str/replace name #"`" "``")
       "`"))

(defmethod escape-attr-name :mssql [db name]
  (str "[" name "]"))

(defmethod escape-attr-name :default [db name]
  (str \"
       (str/replace name #"\"" "\"\"")
       \"))
