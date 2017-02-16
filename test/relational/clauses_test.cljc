(ns relational.clauses-test
  (:require [clojure.test :refer :all]
            [relational.clauses :as clauses]
            [relational.comparisons :as c]
            [relational.selectables :as s]
            [relational.helpers :refer [is-sql]]
            [relational.attribute-scopes :as t]))

(deftest sql-clauses
  (testing "SELECTing attributes"
    (is-sql "SELECT *" (clauses/select))

    (is-sql "SELECT `foo`.`bar`, `foo`.`baz`"
            (clauses/select (s/attribute "foo" "bar") (s/attribute "foo" "baz")))

    (is-sql "SELECT DISTINCT `foo`.`bar`, `foo`.`baz`"
            (clauses/distinct (s/attribute "foo" "bar") (s/attribute "foo" "baz"))))

  (testing "FROM clause"
    (is-sql "" (clauses/from))
    (is-sql "FROM `foos`" (clauses/from (t/table "foos"))))

  (testing "WHERE and HAVING"
    (is-sql "" (clauses/where nil))
    (is-sql "" (clauses/having nil))

    (is-sql "WHERE `users`.`id` IS NULL"
            (clauses/where (c/is-null (s/attribute "users", "id"))))
    (is-sql "HAVING `users`.`id` IS NULL"
            (clauses/having (c/is-null (s/attribute "users", "id")))))

  (testing "GROUP BY"
    (is-sql "" (clauses/group-by))
    (is-sql "GROUP BY `users`.`id`" (clauses/group-by (s/attribute "users" "id"))))

  (testing "ORDER BY"
    (is-sql "" (clauses/order-by))
    (is-sql "ORDER BY `users`.`id`" (clauses/order-by (s/attribute "users" "id"))))

  (testing "JOIN clauses"
    (let [foo (t/table "foo")
          bar (s/attribute "foo" "bar")
          id (s/attribute "bar" "id")
          comp (c/comparison "=" bar id)]
      (is-sql "" (clauses/inner-join foo nil))
      (is-sql "INNER JOIN `foo` ON `foo`.`bar` = `bar`.`id`" (clauses/inner-join foo comp))
      (is-sql "LEFT JOIN `foo` ON `foo`.`bar` = `bar`.`id`" (clauses/left-join foo comp))
      (is-sql "RIGHT JOIN `foo` ON `foo`.`bar` = `bar`.`id`" (clauses/right-join foo comp))))

  (testing "Full Select"
    (is-sql "SELECT 'users'" (clauses/query :select ["users"]))
    (is-sql "SELECT `users`.`id`" (clauses/query :select [:users.id]))))
