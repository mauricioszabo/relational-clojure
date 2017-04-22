(ns relational.selectables-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [relational.helpers :refer [sql]]
            [relational.alias :as alias]
            [relational.core :as core]
            [relational.selectables :as attr]
            [relational.attribute-scopes :as scope]))

(deftest selectables
  (let [my-db {:adapter :mysql}
        db {:adapter :sqlite3}]

    (testing "escaping attributes"
      (are [sql table name adapter] (= sql (core/to-pseudo-sql
                                             (attr/attribute table name)
                                             {:adapter adapter}))
        "\"foo\".\"bar\"" "foo" "bar" :sqlite3
        "`foo`.`bar`" "foo" "bar" :mysql))))

(facts "about attributes"
  (fact "escapes literal attributes"
    (attr/literal "foo") => (sql "'foo'")
    (alias/alias (attr/literal "foo") "bar") => (sql "'foo' bar"))

  (fact "escapes attributes"
    attr/all => (sql "*")
    (attr/attribute "foo") => (sql "`foo`")
    (attr/attribute "foo" "*") => (sql "`foo`.*")
    (attr/attribute "foo" "bar") => (sql "`foo`.`bar`")
    (attr/literal (attr/attribute "foo" "bar")) => (sql "`foo`.`bar`")
    (attr/attribute (scope/table "foo") "bar") => (sql "`foo`.`bar`")

    (alias/alias (attr/attribute "foo" "bar") "q")
    => (sql "`foo`.`bar` q")))
