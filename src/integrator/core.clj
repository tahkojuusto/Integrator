(ns integrator.core
    (require [integrator.integrate :as integrate])
    (require [integrator.report :as report])
    (require [integrator.util :as util]))

(defn -run
    "Calculate the integration for each method, and create corresponding reports."
    [f x0 x1 N]

    ; Run separately using different methods.
    (let [trapezoid-result  (integrate/integrate f x0 x1 "trapezoid" N)
          midpoint-result   (integrate/integrate f x0 x1 "midpoint" N)
          simpson-result    (integrate/integrate f x0 x1 "simpson" N)
          trapezoid-report  (report/create-JSON-method-struct "trapezoid" trapezoid-result)
          midpoint-report   (report/create-JSON-method-struct "midpoint" midpoint-result)
          simpson-report    (report/create-JSON-method-struct "simpson" simpson-result)
          config            (report/create-JSON-config-struct x0 x1 N)]
        (report/create-report [trapezoid-report midpoint-report simpson-report] config)))

(defn -main
    "Integrate f(x) from x0 to x1 with N steps. Use multiple numerical methods.
    From results, create a JSON report."
    [& args]
    (if (not (= (count args) 3))
        (println "Usage: lein run <x0> <x1> <N>")
            (let [f         (util/parse-fn "") ; TODO: make this input.
                  [x0 x1 N] (util/parse-args args)]
                (println (-run f x0 x1 N)))))