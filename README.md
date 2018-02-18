# Integrator

## Overview

This app calculates the two-dimensional integration numerically using three methods:
- The midpoint rule
- The trapezoidal rule
- Simpson's rule

## Implementation

![Integrator](architecture.png?raw=true "Integrator")

Integrator has layered architecture, i.e. a high-level layer or module refers to low-level layer, which in turn, refers to a more primitive layer. In lower layers, there are no references back to higher layers.

``core/-main`` function invokes the parser in order to turn function of two variables f(x, y) from string (infix) to Clojure procedure (prefix). The grammar rules are

```
start --> expr + start (1) | expr - start (2) | expr (3)
expr  --> func * expr  (4) | func / expr  (5) | func (6)
func  --> FUNC(start)  (7) | fact ^ fact  (8) | fact (9)
fact  --> (start)      (10)| DOUBLE      (11) | - DOUBLE (12)
```

where lowercase keywords are non-terminals and uppercase keywords terminals. While the grammar is ``LL(1)``, the parser utilizes the backtracking algorithm. Lexical analyzer creates tokens needed by the parser, and removes redundant symbols such as whitespaces.

The main function passes the parsed function and configuration information to ``integrate/integrate`` function, which runs the integration using the given numerical method.

``report/create-report`` creates a JSON report from the results and configuration.

## Installation

1. Install Clojure: ``brew install clojure``
2. Clone the repository: ``git clone git@github.com:tahkojuusto/Integrator.git``
3. Install dependencies in the project root folder: ``lein deps``

## Usage

The CLI command has the form of ``lein run <function> <x0> <y0> <x1> <y1> <N>``. For example,

```sh
$ lein run "x^(-2*x)*sin(y + exp(y))" 0.1 0.1 1 1 100
```

```clojure
(* (Math/pow x (* -2 x)) (Math/sin (+ y (Math/exp y))))
```

```json
{
    "configuration":
        {
            "sample-size": 100,
            "x0": 0.1,
            "y0": 0.1,
            "x1": 1.0,
            "y1": 1.0
        },
    "methods":
        [
            {
                "method-name": "midpoint", "result":  0.76832
            },
            {
                "method-name": "trapezoid", "result": 0.76824
            },
            {
                "method-name": "simpson", "result": 0.7770
            }
        ]
}
```

## Supported functionality

### Operators

```
+ - / * ^
```

### Math functions

```
sqrt exp ln sin cos
```