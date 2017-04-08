(ns relational.attribute-scopes-test
  (:require [midje.sweet :refer :all]
            [relational.helpers :as h]
            [relational.core :as core]
            [relational.alias :as alias]
            [relational.attribute-scopes :as table]))

(fact "escapes tables"
  (let [my-db {:adapter :mysql}
        db {:adapter :sqlite3}
        sql-for-table #(core/to-pseudo-sql (table/table %1) %2)]
    (sql-for-table "foo" my-db) => "`foo`"
    (sql-for-table "foo" db) => "\"foo\""))

(fact "aliases tables"
  (alias/alias (table/table "table") "alias")
  => (h/sql "`table` alias"))
