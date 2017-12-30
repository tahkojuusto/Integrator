(ns integrator.core
    (:require [integrator.integrate :as integrate])
    (:require [integrator.report :as report])
    (:require [integrator.util :as util])
    (:require [integrator.parser.lex :as lex])
    (:require [integrator.parser.parse :as parse])
    (:require [clojure.tools.logging :as log]))

(defn -run
    "Calculate the integration for each method, and create corresponding reports."
    [f x0 x1 N]
    (log/trace "ENTERING core/-run.")
    ; Run separately using different methods.
    (let [trapezoid-result  (integrate/integrate f x0 x1 "trapezoid" N)
          midpoint-result   (integrate/integrate f x0 x1 "midpoint" N)
          simpson-result    (integrate/integrate f x0 x1 "simpson" N)
          trapezoid-report  (report/create-JSON-method-struct "trapezoid" trapezoid-result)
          midpoint-report   (report/create-JSON-method-struct "midpoint" midpoint-result)
          simpson-report    (report/create-JSON-method-struct "simpson" simpson-result)
          config            (report/create-JSON-config-struct x0 x1 N)]
        (report/create-report [trapezoid-report midpoint-report simpson-report] config)))


(defn -parse-fn
    "Parse string representation of a function to
    Clojure function fn."
    [f-str]
    (log/trace "ENTERING core/-parse-fn.")
    (let [tokens    (lex/scan f-str)
          ast       (parse/parse tokens)]
        (parse/create-fn ast)))

(defn -parse-args
    "Parse string arguments."
    [args]
    (log/trace "ENTERING core/-parse-args.")
    (let [f-str     (nth args 0)
          x0-str    (nth args 1)
          x1-str    (nth args 2)
          N-str     (nth args 3)
          f         (-parse-fn f-str)
          x0        (Long/parseLong x0-str)
          x1        (Long/parseLong x1-str)
          N         (Long/parseLong N-str)]
        [f x0 x1 N]))

(defn -main
    "Integrate f(x) from x0 to x1 with N steps. Use multiple numerical methods.
    From results, create a JSON report."
    [& args]
    (log/trace "ENTERING core/-main.")
    (if (not (= (count args) 4))
        (println "Usage: lein run <f> <x0> <x1> <N>")
            (let [[f x0 x1 N] (-parse-args args)
                  result (-run f x0 x1 N)]
                result)))