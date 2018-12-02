(ns clojurex2018-kafka.four
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
    StreamsConfig/DEFAULT_VALUE_SERDE_CLASS_CONFIG (.getName (.getClass (Serdes/String)))}))

(defn pipe
  [topic]
  (let [builder (StreamsBuilder.)]
    (-> (.stream builder topic)
        (.mapValues (reify
                      ValueMapper
                      (apply [_ value]
                        (try
                          (println "value -->" value)
                          (catch java.lang.ClassCastException e
                            (println "Exception:" e "for value:" value)))
                        [value]))))
    builder))


(defrecord KafkaStream [address consumer-group topic]
  component/Lifecycle
  (start [component]
    (println ";; Starting Kafka stream")
    (if (:stream component)
      component
      (let [kafka-config (kafka-stream-config consumer-group address)
            stream (KafkaStreams. (.build (pipe topic)) kafka-config)
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
  :topic "test"
  :consumer-group "cool-group5"
  :kafka (component/using (new-kafka-server) [:address :consumer-group :topic])))



(def system (new-system))

(defn start
  []
  (alter-var-root #'system component/start))

(defn stop
  []
  (alter-var-root #'system component/stop))
