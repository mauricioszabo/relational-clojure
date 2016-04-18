(ns relational.comparisons-test
  (:require [clojure.test :refer :all]
            [relational.comparisons :as c]
            [relational.selectables :as s]
            [relational.core :refer [to-pseudo-sql]]))

(deftest comparisons
  (let [sql #(to-pseudo-sql % {:adapter :mysql})
        foo (s/attribute "foo" "foo")
        bar (s/attribute "foo" "bar")]

    (testing "equality and difference comparisons"
      (is (= "`foo`.`bar` = 'quox'" (sql (c/comparison "=" bar "quox"))))
      (is (= "`foo`.`foo` = `foo`.`bar`" (sql (c/comparison "=" foo bar))))
      (is (= "`foo`.`foo` != `foo`.`bar`" (sql (c/comparison "!=" foo bar)))))

    (testing "comparission with nil"
      (is (= "`foo`.`bar` IS NULL" (sql (c/is-null bar))))
      (is (= "`foo`.`bar` IS NOT NULL" (sql (c/is-not-null bar)))))

    (testing "comparision with multiple arity"
      (is (nil? (c/comparison "=")))
      (is (= "`foo`.`foo` = `foo`.`bar` AND `foo`.`bar` = 'quox'"
             (sql (c/comparison "=" foo bar "quox")))))

    (testing "comparing with IN or NOT IN"
      (is (= "`foo`.`bar` IN (1,2,3,4)"
             (sql (c/in bar [1 2 3 4])))))))
