(ns clojurex2018-kafka.five
  (:require [gregor.core :as gregor]
            [com.stuartsierra.component :as component])
  (:import [org.apache.kafka.streams.kstream KeyValueMapper ValueMapper]
           [org.apache.kafka.streams KafkaStreams StreamsConfig StreamsBuilder KeyValue]
           [org.apache.kafka.common.serialization Serdes]))


(defn kafka-stream-config
  [consumer-group address]
  (StreamsConfig.
   {StreamsConfig/APPLICATION_ID_CONFIG consumer-group
    StreamsConfig/BOOTSTRAP_SERVERS_CONFIG address
    StreamsConfig/DEFAULT_KEY_SERDE_CLASS_CONFIG (.getName (.getClass (Serdes/String)))
    StreamsConfig/DEFAULT_VALUE_SERDE_CLASS_CONFIG (.getName (.getClass (Serdes/String)))}))

(defn pipe
  [input-topic output-topic]
  (let [builder (StreamsBuilder.)]
    (-> (.stream builder input-topic)
        (.mapValues (reify
                      ValueMapper
                      (apply [_ v]
                        (binding [*read-eval* false]
                          (read-string v)))))
        (.map (reify
                KeyValueMapper
                (apply [_ k v]
                  (do
                    (println "v is" v "and the count is:" (count (str v)))
                    (KeyValue. "counter" (str (count (str v))))))))
        (.to output-topic))
    builder))


(defrecord KafkaStream [address consumer-group input-topic output-topic]
  component/Lifecycle
  (start [component]
    (println ";; Starting Kafka stream")
    (if (:stream component)
      component
      (let [kafka-config (kafka-stream-config consumer-group address)
            stream (KafkaStreams. (.build (pipe input-topic output-topic)) kafka-config)
            _ (.start stream)]
        (assoc component :stream stream))))
  (stop [component]
    (println ";; Stopping Kafka stream")
    (when-let [stream (:stream component)]
      (.close stream))
    (assoc component :stream nil)))

(defn new-kafka-server
  []
  (map->KafkaStream {}))


(defn new-system
 []
 (component/system-map
  :address "localhost:9092"
  :input-topic "test"
  :output-topic "count-topic"
  :consumer-group "cool-group-7"
  :kafka (component/using (new-kafka-server)
                          [:address
                           :consumer-group
                           :input-topic
                           :output-topic])))



(def system (new-system))

(defn start
  []
  (alter-var-root #'system component/start))

(defn stop
  []
  (alter-var-root #'system component/stop))

(comment
  (def producer (gregor/producer "localhost:9092"))
  (gregor/send producer "test" "abc-def")

  (def consumer (gregor/consumer "localhost:9092" "testgroup" ["count-topic"]))

  (gregor/poll consumer))
