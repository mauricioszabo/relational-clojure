(ns relational.sexp
  (:require [relational.attribute-scopes :as scope]
            [relational.selectables :as selectable]
            [relational.comparisons :as comparisions]
            [relational.clauses :as clauses]
            [relational.alias :as alias]))

(defmulti convert
  "Converts a SEXP structure into a relational one."
  (fn [sexp] (first (filter #(get sexp %)
                            [:select :attribute :table]))))

(defmethod convert nil [sexp]
  (cond
    (and (coll? sexp) (empty? sexp)) nil
    (nil? sexp) nil
    :else (selectable/literal sexp)))

(defmethod convert :table [sexp]
  (let [table (scope/table (get-in sexp [:table :name] (:table sexp)))
        alias-name (get-in sexp [:table :alias])]
    (cond-> table
            alias-name (alias/alias alias-name))))

(defmethod convert :attribute [sexp]
  (let [table (convert (dissoc sexp :attribute))
        attr (get-in sexp [:attribute :name] (:attribute sexp))
        alias-name (get-in sexp [:attribute :alias])
        selectable (if (nil? table)
                     (if (= attr "*") selectable/all (selectable/attribute attr))
                     (selectable/attribute table attr))]
    (cond-> selectable
            alias-name (alias/alias alias-name))))

(def ^:private comparisions (ns-publics 'relational.comparisons))
(defn- parse-condition [[condition & args]]
  (if-let [comp-fun (get comparisions condition)]
    (apply comp-fun (map #(if (and (coll? %) (symbol? (first %)))
                            (parse-condition %)
                            (convert %))
                         args))
    (throw (ex-info "Not a valid comparision clause" {:comparision condition}))))

(defmethod convert :select [sexp]
  (clauses/->FullSelect (->> sexp :select (map convert) (clauses/->Select false))
                        (some->> sexp :from (map convert) clauses/->From)
                        (some->> sexp :where parse-condition (clauses/->Comparision "WHERE"))
                        ; (->> sexp :joins (map convert) clauses/->Join)
                        nil
                        (some->> sexp :group (map convert) clauses/->GroupBy)
                        (some->> sexp :having parse-condition (clauses/->Comparision "HAVING"))
                        (some->> sexp :order (map convert) clauses/->OrderBy)
                        ; (->> sexp :limit (map convert) clauses/->From)
                        nil
                        ; (->> sexp :offset (map convert) clauses/->From)))
                        nil))
