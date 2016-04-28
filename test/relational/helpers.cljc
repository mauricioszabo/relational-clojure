(ns relational.helpers
  (:require [relational.core :refer [to-pseudo-sql]]
            [clojure.test :refer [is]]))

(defn is-sql [sql partial]
  (let [sql-fn #(to-pseudo-sql % {:adapter :mysql})]
    (is (= sql (sql-fn partial)))))
