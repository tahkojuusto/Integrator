(ns integrator.util)

(defn parse-args
    "Parse string argument to number."
    [args]
    (let [x0-str (nth args 0)
          x1-str (nth args 1)
          N-str  (nth args 2)
          x0     (Long/parseLong x0-str)
          x1     (Long/parseLong x1-str)
          N      (Long/parseLong N-str)]
        [x0 x1 N]))

(defn parse-fn
    "Parse string representation of a function to
    Clojure function fn."
    [fn-str]
    (fn [x] (+ (- (* x x) x) 1)))
