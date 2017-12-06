(ns integrator.process)

(defn -integrate-trapezoid
    "Calculate integrate of f(x) (i.e. area A) from x0 to x1 using interval A.
    Use the trapezoid rule."
    [f x0 x1 x dx A]

    ; Iterate until x reaches the upper limit x1.
    (if (>= x x1)
        A
        (let [f0 (f x)
              f1 (f (+ x dx))
              dA (* (/ (+ f0 f1) 2) dx)]
            (recur f x0 x1 (+ x dx) dx (+ A dA)))))

(defn -integrate-midpoint
    "Calculate integrate of f(x) (i.e. area A) from x0 to x1 using interval A.
    Use the midpoint rule."
    [f x0 x1 x dx A]

    ; Iterate until x reaches the upper limit x1.    
    (if (>= x x1)
        A
        (let [fm (f (+ x (/ dx 2)))
              dA (* fm dx)]
            (recur f x0 x1 (+ x dx) dx (+ A dA)))))


(defn -integrate-simpson-coefficient
    "Determine coefficient (2 or 4) in the middle terms of Simpson rule."
    [i]
    (let [even? (= (mod i 2) 0)]
        (if even? 2 4)))

(defn -integrate-simpson
    "Calculate integrate of f(x) (i.e. area A) from x0 to x1 using interval A.
    Use the Simpson rule."
    [f x0 x1 x dx A i]

    ; Iterate until x reaches the upper limit x1.
    (if (>= x x1)
        A
        (let [coef (-integrate-simpson-coefficient i)
              f0 (f x0)
              f1 (f x1)
              fm (* coef (f x))
              dA (cond (= x x0) (/ (* f0 dx) 3)
                       (= x x1) (/ (* f1 dx) 3)
                       :else (/ (* fm dx) 3))]
            (recur f x0 x1 (+ x dx) dx (+ A dA) (+ i 1)))))

(defn integrate
    "Integrate f(x) from x0 to x1. Use the given method with N samples."
    [f x0 x1 method N]
    (let [dx (/ (- x1 x0) N)
          A0 0
          i0 0
          result (cond (= method "trapezoid") (-integrate-trapezoid f x0 x1 x0 dx A0)
                       (= method "midpoint") (-integrate-midpoint f x0 x1 x0 dx A0)
                       (= method "simpson") (-integrate-simpson f x0 x1 x0 dx A0 i0)
                       :else "ERR: Specific method not found!")]
        (str method ":\t" (double result))))