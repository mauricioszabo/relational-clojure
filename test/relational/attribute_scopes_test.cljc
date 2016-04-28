(ns relational.attribute-scopes-test
  (:require [clojure.test :refer :all]
            [relational.core :as core]
            [relational.attribute-scopes :as table]))

(deftest tables
  (let [my-db {:adapter :mysql}
        db {:adapter :sqlite3}]

    (testing "escaping tables"
      (are [sql name adapter] (= sql (core/to-pseudo-sql (table/table name)
                                                         {:adapter adapter}))
        "\"foo\"" "foo" :sqlite3
        "`foo`" "foo" :mysql))))
