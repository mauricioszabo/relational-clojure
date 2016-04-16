(ns relational.core-test
  (:require [clojure.test :refer :all]
            [relational.core :refer :all]))

(deftest partial-test
  (let [num-partial (reify IPartial (partial-fn [this]
                                      (fn [_] ["a = ?" [10]])))
        str-partial (reify IPartial (partial-fn [this]
                                      (fn [_] ["b = ?" ["it's me"]])))
        db {:adapter :sqlite3}]

    (testing "Generates a pseudo-SQL"
      (is (= "a = 10" (to-pseudo-sql num-partial db)))
      (is (= "b = 'it''s me'" (to-pseudo-sql str-partial db))))

    (testing "combining partials"
      (is (= ["a = ? OR b = ?", [10, "it's me"]]
             ((combine-partials-with " OR " num-partial str-partial) db))))))

(run-tests)
