(ns relational.sexp
  (:require [relational.attribute-scopes :as scope]
            [relational.selectables :as selectable]
            [relational.alias :as alias]))

(defmulti convert
  "Converts a SEXP structure into a relational one."
  (fn [sexp] (first (filter #(get sexp %)
                            [:attribute :table]))))

(defmethod convert nil [_])

(defmethod convert :table [sexp]
  (let [table (scope/table (get-in sexp [:table :name] (:table sexp)))
        alias-name (get-in sexp [:table :alias])]
    (cond-> table
            alias-name (alias/alias alias-name))))

(defmethod convert :attribute [sexp]
  (let [table (convert (dissoc sexp :attribute))
        attr (get-in sexp [:attribute :name] (:attribute sexp))
        alias-name (get-in sexp [:attribute :alias])]
    (if (and (nil? table) (= attr "*"))
      selectable/all
      (cond-> (selectable/attribute table attr)
              alias-name (alias/alias alias-name)))))
