# Integrator

## Overview

This app calculates the integration numerically using three methods:
- The midpoint rule
- The trapezoidal rule
- Simpson's rule

## Implementation

![Integrator](architecture.png?raw=true "Integrator")

Integrator has layered architecture, i.e. a high-level layer or module refers to low-level layer, which in turn, refers to a more primitive layer. In lower layers, there are no references back to higher layers.

``core/-main`` function invokes the parser in order to turn function of one variable f(x) from string (infix) to Clojure procedure (prefix). The grammar rules are

```
start --> expr + start (1) | expr - start (2) | expr (3)
expr  --> fact * expr  (4) | fact / expr  (5) | fact (6)
fact  --> (start)      (7) | INTEGER       (8)
```

where lowercase keywords are non-terminals and uppercase keywords terminals. While the grammar is ``LL(1)``, the parser utilizes the backtracking algorithm. Lexical analyzer creates tokens needed by the parser, and removes redundant symbols such as whitespaces.

The main function passes the parsed function and configuration information to ``integrate/integrate`` function, which runs the integration using the given numerical method.

``report/create-report`` creates a JSON report from the results and configuration.

## Installation

1. Install Clojure: ``brew install clojure``
2. Clone the repository: ``git clone git@github.com:tahkojuusto/Integrator.git``
3. Install dependencies in the project root folder: ``lein deps``

## Usage

The CLI command has the form of ``lein run <function> <x0> <x1> <N>``. For example,

```sh
$ lein run "x*x - 1" 0 1 100
```

```json
=> {"configuration": {"sample-size": 100, "x0": 0, "x1": 1},
    "methods":       [{"method-name": "trapezoid", "result": -0.66665},
                      {"method-name": "midpoint",  "result": -0.666675},
                      {"method-name": "simpson",   "result": -0.6666666666666667}]}
```