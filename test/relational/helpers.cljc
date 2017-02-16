(ns relational.helpers
  (:require [relational.core :refer [to-pseudo-sql]]
            [midje.checkers :as checkers]
            [clojure.test :refer [is]]))

(defn is-sql [sql partial]
  (let [sql-fn #(to-pseudo-sql % {:adapter :mysql})]
    (is (= sql (sql-fn partial)))))

(defn sql [sql]
  (checkers/chatty-checker [partial]
    (= sql
       (to-pseudo-sql partial {:adapter :mysql}))))
