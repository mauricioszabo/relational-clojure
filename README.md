# Relational Clojure

An experiment to abstract SQL fragments into Clojure constructions.

## Usage

*WARNING - Not working yet!*

Most constructions are aliased in the `sql` namespace. You can run with:

```clojure
(use '[relational.sql])

; SELECT "foo"."bar"
(sql/select (sql/attribute "foo" "bar"))

; WHERE "foo"."bar" = 'baz' OR "foo"."bar" = 'quox'
(sql/where
  (sql/or
    (sql/= (sql/attribute "foo" "bar") "baz")
    (sql/= (sql/attribute "foo" "bar") "quox")))

; NILs are supported - they are ignored
(sql/where nil) ; => ""
(sql/where
  (sql/or
    (sql/= (sql/attribute "foo" "bar") "baz")
    nil)) ; => WHERE "foo"."bar" = 'baz'
```

But it is tedious to do this all the time. So, we have a bunch of macros in `relational.core` to help:

```clojure
(use '[relational.core :refer :all])

; SELECT "foo"."bar" FROM "foo" WHERE "foo"."bar" = 'baz'
(query
  (select :foo.bar)
  (from :foo)
  (where (= :foo.bar "baz")))
```

But, Relational constructions doesn't return strings. They return `IPartial`s, constructions that respond to `partial-fn`. These return a function that expect a single parameter - `db` - that are hashes used to configure a database connection.

When we run this function, it'll return a tuple (a vector with 2 positions) - a SQL string with placeholders `?`, and another vector with the parameters.

```clojure
(use '[relational.core :refer :all])

(db-for mysql :adapter :mysql
              :host "localhost"
              :user "root")

(db-for pg :adapter :postgresql
           :host "localhost"
           :user "postgres")

; SELECT "foo"."bar" FROM "foo" WHERE "foo"."bar" = 'baz'
(def q (query
         (select :foo.bar)
         (from :foo)
         (where (= :foo.bar "baz"))))

(def p (partial-fn q))

; => ["SELECT `foo`.`bar` FROM `foo` WHERE `foo`.`bar` = ?" ["baz"]]
(p mysql)

; => ["SELECT \"foo\".\"bar\" FROM \"foo\" WHERE \"foo\".\"bar\" = ?" ["baz"]]
(p pg)
```

## Compositions

TODO

## License

Copyright © 2016 Maurício Szabo

Distributed under the MIT License.
