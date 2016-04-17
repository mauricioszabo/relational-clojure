(ns relational.attribute-scopes
  (:require [relational.escapes :refer [escape-attr-name]]
            [relational.core :as c]))

(defrecord Table [name]
  c/IPartial
  (partial-fn [this] (fn [db] [(escape-attr-name db (:name this)) nil])))

