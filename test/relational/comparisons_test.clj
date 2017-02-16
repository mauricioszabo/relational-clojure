(ns relational.comparisons-test
  (:require [midje.sweet :refer :all]
            [relational.comparisons :as c]
            [relational.selectables :as s]
            [relational.helpers :refer [is-sql sql]]
            [relational.core :refer [to-pseudo-sql]]))

(def foo (s/attribute "foo" "foo"))
(def bar (s/attribute "foo" "bar"))

(facts "about SQL comparisions"
  (fact "generates equality comparisions"
    (c/comparison "=" bar "quox") => (sql "`foo`.`bar` = 'quox'")
    (c/comparison "=" bar "quox") => (sql "`foo`.`bar` = 'quox'")
    (c/comparison "=" foo bar) => (sql "`foo`.`foo` = `foo`.`bar`")
    (c/comparison "!=" foo bar) => (sql "`foo`.`foo` != `foo`.`bar`")
    (c/= foo bar) => (sql "`foo`.`foo` = `foo`.`bar`")
    (c/not= foo bar) => (sql "`foo`.`foo` != `foo`.`bar`"))

  (fact "compares with nil"
    (c/nil? bar) => (sql "`foo`.`bar` IS NULL")
    (c/not-nil? bar) => (sql "`foo`.`bar` IS NOT NULL"))

  (fact "compares with multiple arity"
    (c/comparison "=") => nil?
    (c/comparison "=" foo bar "quox")
    => (sql "`foo`.`foo` = `foo`.`bar` AND `foo`.`bar` = 'quox'"))

  (fact "compares with IN or NOT IN"
    (c/in bar [1 2 3 4]) => (sql "`foo`.`bar` IN (1,2,3,4)")
    (c/not-in bar [1 2 3 4]) (sql "`foo`.`bar` NOT IN (1,2,3,4)"))

  (fact "compares with multi-operator"
    (c/-> bar [1 2 3 4]) => (sql "`foo`.`bar` IN (1,2,3,4)")
    (c/-> bar []) => (sql "`foo`.`bar` IS NULL")
    (c/-> bar nil) => (sql "`foo`.`bar` IS NULL")
    (c/-> bar 200) => (sql "`foo`.`bar` = 200")))
