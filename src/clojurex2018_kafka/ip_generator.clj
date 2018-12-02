(ns clojurex2018-kafka.ip-generator
  (:require [inet.data.ip :as ip]))

(def uk-ip-range {:start "2.24.0.0" :end "2.24.255.255"})

(def ip-ranges [uk-ip-range
                {:start "27.123.212.0" :end "27.123.215.255"} ;; Mongolia
                {:start "41.75.160.0" :end "41.75.175.255"} ;; Uganda
                {:start "202.2.96.0" :end "202.2.127.255"} ;; Tuvalu
                {:start "23.208.193.0" :end "23.208.199.255"} ;; Uruguay
                {:start "8.45.4.0" :end "8.45.5.255"}]) ;; Philippines

(defn get-ip
  "Get a random IP address"
  []
  (let [random-range (nth ip-ranges (rand-int (count ip-ranges)))
        ips (ip/address-range (:start random-range) (:end random-range))
        ip-count (count ips)]
    (-> (nth ips (rand-int ip-count))
        str)))

(defn uk-ip?
  "For the `given-ip` address, return boolean response if the IP is a UK
  IP address."
  [given-ip]
  (let [uk-ips (ip/address-range (:start uk-ip-range) (:end uk-ip-range))]
    (boolean (some #(= % (ip/address given-ip)) uk-ips))))
