(ns relational.attribute-scopes
  (:require [relational.escapes :refer [escape-attr-name]]
            [relational.core :as c]
            [relational.alias :as alias]))

(defrecord TableAlias [table-name alias-name]
  c/IPartial
  (partial-fn [_]
    (fn [db] [(str (escape-attr-name db table-name) " " alias-name)
              nil])))

(defrecord Table [name]
  c/IPartial
  (partial-fn [_]
    (fn [db] [(escape-attr-name db name) nil]))

  alias/IAlias
  (alias [_ alias-name] (->TableAlias name alias-name)))

(defn table [name] (->Table name))
