(ns integrator.core
  (require [integrator.process :as proc]))

(defn -run
    [f x0 x1 N]

    ; Run separately using different methods.
    (let [trapezoid-report (proc/integrate f x0 x1 "trapezoid" N)
          midpoint-report (proc/integrate f x0 x1 "midpoint" N)
          simpson-report (proc/integrate f x0 x1 "simpson" N)]
        (println trapezoid-report)
        (println midpoint-report)
        (println simpson-report)))

(defn -main
    "Integrate f(x) from x0 to x1 with N steps. Use multiple numerical methods."
    [& args]
    (if (not (= (count args) 4))
        (println "Usage: (-main (fn [x] ( ... )) x0 x1 N)")
        (let [[f x0 x1 N] args]
            (-run f x0 x1 N))))