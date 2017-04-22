(ns relational.sexp-test
  (:require [midje.sweet :refer :all]
            [relational.sexp :as sexp]
            [relational.attribute-scopes :as scope]
            [relational.alias :as alias]
            [relational.selectables :as selectable]
            [relational.helpers :refer [sql]]))

(facts "converting SEXPs from SELECT clauses"
  (fact "solves table"
    (sexp/convert {:table "foo"}) => (scope/table "foo")
    (sexp/convert {:table {:name "foo"}})
    => (scope/table "foo")
    (sexp/convert {:table {:name "foo" :alias "bar"}})
    => (alias/alias (scope/table "foo") "bar"))

  (fact "solves attributes"
    (sexp/convert {:attribute "*"}) => selectable/all
    (sexp/convert {:table "foo" :attribute "bar"})
    => (selectable/attribute "foo" "bar")
    (sexp/convert {:attribute "bar"})
    => (selectable/attribute "bar")
    (sexp/convert {:table "foo" :attribute {:name "bar"}})
    => (selectable/attribute "foo" "bar")
    (sexp/convert {:table "foo" :attribute "*"})
    => (selectable/attribute "foo" "*")
    (sexp/convert {:table "foo" :attribute {:name "bar" :alias "b"}})
    => (alias/alias (selectable/attribute "foo" "bar") "b")))

(facts "when generating full queries"
  (fact "will generate a simple query"
    (sexp/convert {:select [{:attribute "bar"}]}) => (sql "SELECT `bar`")

    (sexp/convert {:select [{:attribute "bar"}]
                   :from [{:table "foo"}]})
    => (sql "SELECT `bar` FROM `foo`")))
