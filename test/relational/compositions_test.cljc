(ns relational.compositions-test
  (:require [clojure.test :refer :all]
            [relational.core :as core]
            [relational.selectables :as attr]
            [relational.comparisons :as c]
            [relational.compositions :as cmp]))

(deftest compositions
  (let [sql #(core/to-pseudo-sql % {:adapter :mysql})
        attr (attr/attribute "foo" "bar")
        eqa (c/comparison "=" attr "aaa")
        eqb (c/comparison "=" attr "bbb")]

    (testing "composing with OR"
      (is (= "(`foo`.`bar` = 'aaa')"
             (sql (cmp/compose "OR" eqa))))
      (is (= "(`foo`.`bar` = 'aaa' OR `foo`.`bar` = 'bbb')"
             (sql (cmp/compose "OR" eqa eqb))))
      (is (= "(`foo`.`bar` = 'aaa' OR `foo`.`bar` = 'bbb' OR `foo`.`bar` = 'aaa')"
             (sql (cmp/compose "OR" eqa eqb eqa)))))

    (testing "composing with AND"
      (is (= "`foo`.`bar` = 'aaa'"
             (sql (cmp/compose "AND" eqa))))
      (is (= "`foo`.`bar` = 'aaa' AND `foo`.`bar` = 'bbb'"
             (sql (cmp/compose "AND" eqa eqb))))
      (is (= "`foo`.`bar` = 'aaa' AND `foo`.`bar` = 'bbb' AND `foo`.`bar` = 'aaa'"
             (sql (cmp/compose "AND" eqa eqb eqa)))))

    (testing "AND/OR with nil"
      (is (nil? (cmp/compose "OR")))
      (is (= "(`foo`.`bar` = 'aaa')" (sql (cmp/compose "OR" nil eqa)))))

    (testing "composing with NOT"
      (is (= "NOT(`foo`.`bar` = 'aaa')"
             (sql (cmp/->Not eqa)))))))

(run-tests)
