(ns relational.comparision-test
  (:require [clojure.test :refer :all]
            [relational.core :as core]
            [relational.comparision :as comp]))

(deftest comparisions
  (let [my-db {:adapter :mysql}
        db {:adapter :sqlite3}]

    (testing "escaping tables"
      (are [sql name adapter] (= sql (core/to-pseudo-sql (comp/->Table name)
                                                         {:adapter adapter}))
        "\"foo\"" "foo" :sqlite3
        "`foo`" "foo" :mysql))

    (testing "escaping attributes"
      (are [sql table name adapter] (= sql (core/to-pseudo-sql
                                             (comp/->Attribute (comp/->Table table) name)
                                             {:adapter adapter}))
        "\"foo\".\"bar\"" "foo" "bar" :sqlite3
        "`foo`.`bar`" "foo" "bar" :mysql))

    (testing "comparing with equals or different"
      (is (= "")))))

(run-tests)
