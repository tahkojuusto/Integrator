(ns integrator.report
  (:require [clojure.data.json :as json]))


(defn -json-report
  [data]
  (json/write-str data))


(defn -create-root-data
  [methods-data conf]
  {:configuration {:sample-size (:sample-size conf)
                   :x0          (:x0 conf)
                   :x1          (:x1 conf)}
   :methods methods-data})


(defn create-report
  [methods conf]
  (let [data (-create-root-data methods conf)]
    (-json-report data)))