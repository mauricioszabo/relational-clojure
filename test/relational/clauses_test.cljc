(ns relational.clauses-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [relational.clauses :as clauses]
            [relational.comparisons :as c]
            [relational.selectables :as s]
            [relational.helpers :refer [is-sql sql]]
            [relational.attribute-scopes :as t]))

(def foo (t/table "foo"))
(def bar (s/attribute "foo" "bar"))
(def id (s/attribute "bar" "id"))
(def eq (c/= bar id))

(facts "about query clauses"
  (fact "can SELECT attributes"
    (clauses/select) => (sql "SELECT *")
    (clauses/select (s/attribute "foo" "bar") (s/attribute "foo" "baz"))
    => (sql "SELECT `foo`.`bar`, `foo`.`baz`")
    (clauses/distinct (s/attribute "foo" "bar") (s/attribute "foo" "baz"))
    => (sql "SELECT DISTINCT `foo`.`bar`, `foo`.`baz`"))

  (fact "can choose what table we select FROM"
    (clauses/from) => (sql "")
    (clauses/from (t/table "foos")) => (sql "FROM `foos`"))

  (fact "can filter with WHERE and HAVING"
    (clauses/where nil) => (sql "")
    (clauses/having nil) => (sql "")

    (clauses/where (c/nil? (s/attribute "users", "id")))
    => (sql "WHERE `users`.`id` IS NULL")

    (clauses/having (c/nil? (s/attribute "users", "id")))
    => (sql "HAVING `users`.`id` IS NULL"))

  (fact "GROUPs BY"
    (clauses/group-by) => (sql "")
    (clauses/group-by (s/attribute "users" "id")) => (sql "GROUP BY `users`.`id`"))

  (fact "ORDERs BY"
    (clauses/order-by) => (sql "")
    (clauses/order-by (s/attribute "users" "id")) => (sql "ORDER BY `users`.`id`"))

  (fact "JOINs with other tables"
    (clauses/inner-join foo nil) => (sql "")
    (clauses/inner-join foo eq)
    => (sql "INNER JOIN `foo` ON `foo`.`bar` = `bar`.`id`")
    (clauses/left-join foo eq)
    => (sql "LEFT JOIN `foo` ON `foo`.`bar` = `bar`.`id`")
    (clauses/right-join foo eq)
    => (sql "RIGHT JOIN `foo` ON `foo`.`bar` = `bar`.`id`")))

(facts "when generating a full query"
  (fact "generates a SELECT"
    (clauses/query :select ["users"]) => (sql "SELECT 'users'")
    (clauses/query :select [bar])
    => (sql "SELECT `foo`.`bar`"))

  (fact "generates a FROM"
    (clauses/query :select [bar]
                   :from [foo])
    => (sql "SELECT `foo`.`bar` FROM `foo`"))

  (fact "generates a WHERE / ORDER"
    (clauses/query :select [bar] :from [foo]
                   :where (c/= bar 10)
                   :order [bar])
    => (sql (str "SELECT `foo`.`bar` FROM `foo` "
                 "WHERE `foo`.`bar` = 10 "
                 "ORDER BY `foo`.`bar`")))

  (fact "generates a GROUP BY / HAVING"
    (clauses/query :select [bar] :from [foo]
                   :where (c/= bar 10)
                   :order [bar]
                   :group [bar]
                   :having (c/= bar 20))
    => (sql (str "SELECT `foo`.`bar` FROM `foo` "
                 "WHERE `foo`.`bar` = 10 "
                 "GROUP BY `foo`.`bar` "
                 "HAVING `foo`.`bar` = 20 "
                 "ORDER BY `foo`.`bar`"))))
