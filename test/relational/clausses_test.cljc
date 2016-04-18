(ns relational.clauses-test
  (:require [clojure.test :refer :all]
            [relational.clauses :as clauses]
            [relational.comparisons :as c]
            [relational.selectables :as s]
            [relational.core :refer [to-pseudo-sql]]))

(deftest sql-clauses
  (let [sql #(to-pseudo-sql % {:adapter :mysql})]

    (testing "SELECTing attributes"
      (is (= "SELECT `foo`.`bar`, `foo`.`baz`"
             (sql (clauses/select (s/attribute "foo" "bar") (s/attribute "foo" "baz")))))
      (is (= "SELECT DISTINCT `foo`.`bar`, `foo`.`baz`"
             (sql (clauses/distinct (s/attribute "foo" "bar") (s/attribute "foo" "baz"))))))))
