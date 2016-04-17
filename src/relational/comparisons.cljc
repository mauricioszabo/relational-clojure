(ns relational.comparisons
  (:require [relational.core :refer [IPartial combine-partials-with]]
            [relational.selectables :refer [literal]]))

;; (defrecord IComparison)

(defn comparison [comparison attr1 attr2]
  (reify IPartial
    (partial-fn [this] (combine-partials-with (str " " comparison " ")
                                              (literal attr1)
                                              (literal attr2)))))
