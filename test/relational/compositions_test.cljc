(ns relational.compositions-test
  (:require [clojure.test :refer :all]
            [relational.core :as core]
            [relational.selectables :as attr]
            [relational.comparisons :as c]
            [relational.compositions :as cmp]))

(deftest compositions
  (let [my-db {:adapter :mysql}
        attr (attr/attribute "foo" "bar")
        eqa (c/comparison "=" attr "aaa")
        eqb (c/comparison "=" attr "bbb")]

    (testing "composing with OR"
      (is (= "(`foo`.`bar` = 'aaa')"
             (core/to-pseudo-sql (cmp/compose "OR" eqa) my-db)))
      (is (= "(`foo`.`bar` = 'aaa' OR `foo`.`bar` = 'bbb')"
             (core/to-pseudo-sql (cmp/compose "OR" eqa eqb) my-db)))
      (is (= "(`foo`.`bar` = 'aaa' OR `foo`.`bar` = 'bbb' OR `foo`.`bar` = 'aaa')"
             (core/to-pseudo-sql (cmp/compose "OR" eqa eqb eqa) my-db))))

    (testing "composing with AND"
      (is (= "`foo`.`bar` = 'aaa'"
             (core/to-pseudo-sql (cmp/compose "AND" eqa) my-db)))
      (is (= "`foo`.`bar` = 'aaa' AND `foo`.`bar` = 'bbb'"
             (core/to-pseudo-sql (cmp/compose "AND" eqa eqb) my-db)))
      (is (= "`foo`.`bar` = 'aaa' AND `foo`.`bar` = 'bbb' AND `foo`.`bar` = 'aaa'"
             (core/to-pseudo-sql (cmp/compose "AND" eqa eqb eqa) my-db))))

    (testing "composing with NOT"
      (is (= "NOT(`foo`.`bar` = 'aaa')"
             (core/to-pseudo-sql (cmp/not eqa) my-db))))))
