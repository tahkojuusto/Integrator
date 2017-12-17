# Integrator

This app calculates the integration numerically using three methods:
- The midpoint rule
- The trapezoidal rule
- Simpson's rule

```sh
$ lein run <x0> <x1> <N>
```

```json
=> {"configuration": {"sample-size": 100, "x0": 0, "x1": 1},
    "methods":       [{"method-name": "trapezoid", "result": 0.83335},
                      {"method-name": "midpoint",  "result": 0.833325},
                      {"method-name": "simpson",   "result": 0.83}]}
```