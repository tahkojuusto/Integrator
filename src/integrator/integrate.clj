(ns integrator.integrate)


(defn -step-midpoint
    "Midpoint rule (0th order polynomial)."
    [f state]
    (let [{:keys [x0 x1 x dx sum i]} state
          fm (f (+ x (/ dx 2)))]

        ; Form a horizontal line at the midpoint.
        (* fm dx)))


(defn -step-trapezoid
    "Trapezoid rule (1th order polynomial)."
    [f state]
    (let [{:keys [x0 x1 x dx sum i]} state
          f0 (f x)
          f1 (f (+ x dx))]

        ; Form a line between (x0, f0) - (x1, f1) and take its
        ; value at the midpoint.
        (* (/ (+ f0 f1) 2) dx)))


(defn -integrate-simpson-coefficient
    "Determine coefficient of a middle term in Simpson rule (2 or 4)."
    [i]
    (let [even? (= (mod i 2) 0)]
        (if even? 2 4)))


(defn -step-simpson
    "Simpson rule (2nd order polynomial)."
    [f state]
    (let [{:keys [x0 x1 x dx sum i]} state
          c     (-integrate-simpson-coefficient i)
          f0    (f x0)
          f1    (f x1)
          fm    (* c (f x))]

        ; Form 2nd order polynomial between (x0, f0) - (x1, f1),
        ; and take its value at the midpoint.
        (cond (= x x0) (/ (* f0 dx) 3)
              (= x x1) (/ (* f1 dx) 3)
              :else (/ (* fm dx) 3))))


(defn -integrate-generic
    "Sum steps calculated using specific method until x >= x1."
    [f step state]
    (let [{:keys [x0 x1 x dx sum i]} state]
        (if (>= x x1)
            sum

            ; Recursion with an updated state.
            ; The updated state has accumulated the new step value.
            (recur f step (-> (assoc state :sum (+ sum (step f state)))
                              (assoc :x (+ x dx))
                              (assoc :i (+ i 1)))))))


(defn -init-state
    "Create state map, which has start point x0, end point x1, step size dx, current position x,
     current area sum, and current index i."
    [x0 x1 dx]
    {:x0 x0, :x1 x1, :dx dx, :x x0, :sum 0, :i 0})


(def -method-conversion
    {"trapezoid" -step-trapezoid,
     "midpoint" -step-midpoint,
     "simpson" -step-simpson})


(defn integrate
    "Integrate f(x) from x0 to x1. Use the given method with N samples."
    [f x0 x1 method-str N]
    (let [dx (/ (- x1 x0) N)
          ; Get the initial state map.
          state (-init-state x0 x1 dx)
          ; Get the function pointer based on the function name.
          step-method (get -method-conversion method-str)
          ; Calculate the integration.
          result (-integrate-generic f step-method state)]
        ; Print the result.
        (double result)))
