(ns clojurex2018-kafka.one
  (:require [gregor.core :as gregor]))

(comment

  (def topic "test")
  (def consumer (gregor/consumer "localhost:9092" "testgroup" [topic]))
  (def producer (gregor/producer "localhost:9092"))

  (gregor/poll consumer)

  (gregor/send producer topic "foo")

  ;; look at the message received
  (gregor/poll consumer)

  ({:value "foo"
 :key nil
 :partition 0
 :topic "test"
 :offset 0
 :timestamp 1543750823002
 :timestamp-type "CreateTime"})

  (gregor/send producer topic "foo" "{:a 1 :b 2}")

  ;; look at the message now
  (gregor/poll consumer)

  ({:value "{:a 1 :b 2}"
    :key "foo"
    :partition 0
    :topic "test"
    :offset 1
 :timestamp
  1543750869831
 :timestamp-type "CreateTime"}))
