(ns relational.comparisons-test
  (:require [clojure.test :refer :all]
            [relational.comparisons :as c]
            [relational.selectables :as s]
            [relational.core :refer [to-pseudo-sql]]))

(deftest comparisons
  (testing "equality and difference comparisons"
    (let [foo (s/attribute "foo" "foo")
          bar (s/attribute "foo" "bar")]
      (is (= "`foo`.`bar` = 'quox'" (to-pseudo-sql (c/comparison "=" bar "quox") {:adapter :mysql})))
      (is (= "`foo`.`foo` = `foo`.`bar`" (to-pseudo-sql (c/comparison "=" foo bar) {:adapter :mysql}))))))
