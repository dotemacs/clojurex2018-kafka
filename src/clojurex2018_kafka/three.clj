(ns clojurex2018-kafka.three
  (:require [gregor.core :as gregor]))

(comment

  (gregor/topics {:connection-string "localhost:2181"})

  (gregor/create-topic {:connection-string "localhost:2181"} "extra-cool-topic" {}))
