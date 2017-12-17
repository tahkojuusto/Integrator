(ns integrator.report
    (:require [clojure.data.json :as json]))

(defn -stringify-JSON-report
    "Print the JSON representation of integration results."
    [data]
    (json/pprint-json data))

(defn create-JSON-method-struct
    "Create a JSON structure representing the integration method
    and its result."
    [method-name result]
    {:method-name method-name
     :result      result})

(defn create-JSON-config-struct
    "Create a JSON structure representing the configuration
    settings."
    [x0 x1 N]
    {:x0          x0
     :x1          x1
     :sample-size N})

(defn create-JSON-report
    "Combine method and configuration JSON structures into one
    structure."
    [methods config]
    {:configuration {:sample-size (:sample-size config)
                     :x0          (:x0 config)
                     :x1          (:x1 config)}
     :methods methods})

(defn create-report
    "Create report for integration results."
    [methods config]
    (-stringify-JSON-report (create-JSON-report methods config)))