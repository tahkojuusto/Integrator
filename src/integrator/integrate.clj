(ns integrator.integrate
    (:require [clojure.tools.logging :as log]))

(defn -step-midpoint
    "Midpoint rule (0th order polynomial)."
    [f state]
    (log/trace "ENTERING integrate/-step-midpoint.")
    (let [{:keys [x0 y0 x1 y1 x y dx dy sum i]} state
          fm (f (+ x (/ dx 2)) (+ y (/ dy 2)))
          dx (if (= dx 0) 1 dx)
          dy (if (= dy 0) 1 dy)]

        ; Form a horizontal line at the midpoint.
        (* fm dx dy)))

(defn -step-trapezoid
    "Trapezoid rule (1th order polynomial)."
    [f state]
    (log/trace "ENTERING integrate/-step-trapezoid.")
    (let [{:keys [x0 y0 x1 y1 x y dx dy sum i]} state
          f0 (f x y)
          f1 (f (+ x dx) (+ y dy))

          dx (if (= dx 0) 1 dx)
          dy (if (= dy 0) 1 dy)]

        ; Form a line between (x0, f0) - (x1, f1) and take its
        ; value at the midpoint.
        (* (/ (+ f0 f1) 2) dx dy)))

(defn -integrate-simpson-coefficient
    "Determine coefficient of a middle term in Simpson rule (2 or 4)."
    [i]
    (log/trace "ENTERING integrate/-integrate-simpson-coefficient.")
    (let [even? (= (mod i 2) 0)]
        (if even? 2 4)))

(defn -step-simpson
    "Simpson rule (2nd order polynomial)."
    [f state]
    (log/trace "ENTERING integrate/-step-simpson.")
    (let [{:keys [x0 y0 x1 y1 x y dx dy sum i]} state
          c     (-integrate-simpson-coefficient i)
          f0    (f x0 y0)
          f1    (f x1 y1)
          fm    (* c (f x y))

          dx (if (= dx 0) 1 dx)
          dy (if (= dy 0) 1 dy)]

        ; Form 2nd order polynomial between (x0, f0) - (x1, f1),
        ; and take its value at the midpoint.
        (cond (= x x0) (/ (* f0 dx dy) 3)
              (= x x1) (/ (* f1 dx dy) 3)
              :else (/ (* fm dx dy) 3))))

(defn -integrate-generic
    "Sum steps calculated using specific method until x >= x1."
    [f step state]
    (log/trace "ENTERING integrate/-integrate-generic.")
    (let [{:keys [x0 y0 x1 y1 x y dx dy sum i]} state]
        (if (and (>= x x1) (>= y y1))
            sum

            (if (>= x x1)
                ; Recursion with an updated state.
                ; The updated state has accumulated the new step value.
                (recur f step (-> (assoc state :sum (+ sum (step f state)))
                                  (assoc :x x0)
                                  (assoc :y (+ y dy))
                                  (assoc :i (+ i 1))))

                (recur f step (-> (assoc state :sum (+ sum (step f state)))
                                  (assoc :x (+ x dx))
                                  (assoc :y y)
                                  (assoc :i (+ i 1))))))))

(defn -init-state
    "Create state map, which has start point x0, end point x1, step size dx, current position x,
     current area sum, and current index i."
    [x0 y0 x1 y1 dx dy]
    (log/trace "ENTERING integrate/-init-state.")
    {:x0 x0, :y0 y0, :x1 x1, :y1 y1, :dx dx, :dy dy, :x x0, :y y0, :sum 0, :i 0})

(def -method-conversion
    {"trapezoid" -step-trapezoid,
     "midpoint" -step-midpoint,
     "simpson" -step-simpson})

(defn integrate
    "Integrate f(x) from x0 to x1. Use the given method with N samples."
    [f x0 y0 x1 y1 method-str N]
    (log/trace "ENTERING integrate/integrate.")
    (let [dx (/ (- x1 x0) N)
          dy (/ (- y1 y0) N)

          ; Get the initial state map.
          state (-init-state x0 y0 x1 y1 dx dy)
          ; Get the function pointer based on the function name.
          step-method (get -method-conversion method-str)
          ; Calculate the integration.
          result (-integrate-generic f step-method state)]
        ; Print the result.
        (double result)))
