(ns relational.selectables-test
  (:require [clojure.test :refer :all]
            [relational.core :as core]
            [relational.selectables :as attr]))

(deftest selectables
  (let [my-db {:adapter :mysql}
        db {:adapter :sqlite3}]

    (testing "escaping attributes"
      (are [sql table name adapter] (= sql (core/to-pseudo-sql
                                             (attr/attribute table name)
                                             {:adapter adapter}))
        "\"foo\".\"bar\"" "foo" "bar" :sqlite3
        "`foo`.`bar`" "foo" "bar" :mysql))

    (testing "STAR attributes"
      (is (= "*" (core/to-pseudo-sql attr/all my-db)))
      (is (= "`foo`.*" (core/to-pseudo-sql (attr/attribute "foo" "*") my-db))))

    (testing "literal attributes like strings, numbers"
      (is (= "'foo'" (core/to-pseudo-sql (attr/literal "foo") my-db)))
      (is (= "`foo`.`bar`" (core/to-pseudo-sql
                             (attr/literal (attr/attribute "foo" "bar"))
                             my-db))))))
