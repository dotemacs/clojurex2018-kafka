(ns clojurex2018-kafka.two
  (:require [gregor.core :as gregor]))

(comment

  (do
    (def topic "test")
    (def consumer (gregor/consumer "localhost:9092" "testgroup" [topic]))
    (def consumer2 (gregor/consumer "localhost:9092" "second" [topic]))
    (def producer (gregor/producer "localhost:9092")))

  (map #(gregor/close %) [consumer consumer2 producer])

  (gregor/poll consumer)
  (gregor/poll consumer2)

  (gregor/send producer topic "second-thing")

  (gregor/send producer topic "foo" "{:a 1 :b 2}"))
