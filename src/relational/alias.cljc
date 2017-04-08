(ns relational.alias
  (:refer-clojure :exclude [alias]))

(defprotocol IAlias
  (alias [self alias-name]))
