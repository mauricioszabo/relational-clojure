(ns relational.selectables
  (:require [relational.escapes :refer [escape-attr-name]]
            [relational.attribute-scopes :as scopes]
            [relational.alias :as alias]
            [relational.core :as c])
  (:import [relational.attribute_scopes Table]))

(defprotocol ISelectable
  (select-partial-fn [this]))

(defn- partial-for-attr [table name]
  (fn [db]
    (let [[t-sql t-attrs] ((c/partial-fn table) db)
          my-sql (if (= name "*") "*" (escape-attr-name db name))]
      [(str t-sql "." my-sql) t-attrs])))

(defrecord AliasedAttribute [attribute alias-name]
  c/IPartial
  (partial-fn [_]
    (fn [db]
      (let [[sql attrs] ((c/partial-fn attribute) db)]
        [(str sql " " alias-name) attrs]))))

(defrecord Attribute [table name]
  c/IPartial
  (partial-fn [_] (partial-for-attr table name))

  ISelectable
  (select-partial-fn [this] (c/partial-fn this))

  alias/IAlias
  (alias [this alias-name] (->AliasedAttribute this alias-name)))

(defrecord IndeterminateAttribute [name]
  c/IPartial
  (partial-fn [_]
    (fn [db] [(escape-attr-name db name) []]))

  ISelectable
  (select-partial-fn [this] (c/partial-fn this))

  alias/IAlias
  (alias [this alias-name] (->AliasedAttribute this alias-name)))

(defrecord Literal [attr]
  c/IPartial
  (partial-fn [_] (fn [_] ["?" [attr]]))

  ISelectable
  (select-partial-fn [this] (c/partial-fn this))

  alias/IAlias
  (alias [this alias-name] (->AliasedAttribute this alias-name)))

(defn attribute
  ([attr-name] (->IndeterminateAttribute attr-name))
  ([table-name attr-name]
   (if (instance? Table table-name)
     (->Attribute table-name attr-name)
     (->Attribute (scopes/->Table table-name) attr-name))))

(defn literal [attr-or-partial]
  (if (satisfies? c/IPartial attr-or-partial)
    attr-or-partial
    (->Literal attr-or-partial)))

(def all
  (reify
    c/IPartial (partial-fn [_] (fn [_] ["*" nil]))
    ISelectable (select-partial-fn [_] (fn [_] ["*" nil]))))
