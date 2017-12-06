(ns integrator.core
  (require [integrator.process :as proc]))

(defn -run
    [f x0 x1 N]
    (let [trapezoid-report (proc/integrate f x0 x1 "trapezoid" N)
          midpoint-report (proc/integrate f x0 x1 "midpoint" N)
          simpson-report (proc/integrate f x0 x1 "simpson" N)]
        (println trapezoid-report)
        (println midpoint-report)
        (println simpson-report)))

(defn -main
    "I don't do a whole lot ... yet."
    [& args]
    (let [[f x0 x1 N] args]
        (-run f x0 x1 N)))