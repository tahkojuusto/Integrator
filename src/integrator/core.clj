(ns integrator.core
  (require [integrator.process :as proc])
  (require [integrator.report :as report]))


(defn -create-method-struct
  [method-name result]
  {:method-name method-name
   :result result})

(defn -create-conf-struct
  [x0 x1 N]
  {:x0 x0, :x1 x1, :sample-size N})

(defn -run
    [f x0 x1 N]

    ; Run separately using different methods.
    (let [trapezoid-report (-create-method-struct "trapezoid" (proc/integrate f x0 x1 "trapezoid" N))
          midpoint-report (-create-method-struct "midpoint" (proc/integrate f x0 x1 "midpoint" N))
          simpson-report (-create-method-struct "simpson" (proc/integrate f x0 x1 "simpson" N))]
        (report/create-report [trapezoid-report midpoint-report simpson-report] (-create-conf-struct x0 x1 N))))

(defn -main
    "Integrate f(x) from x0 to x1 with N steps. Use multiple numerical methods."
    [& args]
    (if (not (= (count args) 4))
        (println "Usage: (-main (fn [x] ( ... )) x0 x1 N)")
        (let [f (nth args 0)
              x0 (nth args 1)
              x1 (nth args 2)
              N (nth args 3)]
          (println (-run (fn [x] (* x x)) x0 x1 N)))))