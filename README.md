# Integrator

This app calculates the integration numerically using three methods:
- The midpoint rule
- The trapezoidal rule
- Simpson's rule

Usage: ``lein run <function> <x0> <x1> <N>``.

```sh
$ lein run "x*x - 1" 0 1 100
```

```json
=> {"configuration": {"sample-size": 100, "x0": 0, "x1": 1},
    "methods":       [{"method-name": "trapezoid", "result": -0.66665},
                      {"method-name": "midpoint",  "result": -0.666675},
                      {"method-name": "simpson",   "result": -0.6666666666666667}]}
```