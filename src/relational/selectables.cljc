(ns relational.selectables
  (:require [relational.escapes :refer [escape-attr-name]]
            [relational.attribute-scopes :as scopes]
            [relational.core :as c]))

(defprotocol ISelectable
  (select-partial-fn [this]))

(defrecord Attribute [table name]
  c/IPartial
  (partial-fn [this]
    (fn [db]
      (let [[t-sql t-attrs] ((c/partial-fn table) db)
            my-sql (if (= name "*") "*" (escape-attr-name db name))]
        [(str t-sql "." my-sql) t-attrs])))

  ISelectable
  (select-partial-fn [this] (c/partial-fn this)))

(defrecord Literal [attr]
  c/IPartial
  (partial-fn [this]
    (fn [_] ["?" [attr]]))

  ISelectable
  (select-partial-fn [this] (c/partial-fn this)))

(defn attribute [table-name attr-name]
  (->Attribute (scopes/->Table table-name) attr-name))

(defn literal [attr-or-partial]
  (if (satisfies? c/IPartial attr-or-partial)
    attr-or-partial
    (->Literal attr-or-partial)))

(def all
  (reify
    c/IPartial
    (partial-fn [_] (fn [_] ["*" nil]))))
