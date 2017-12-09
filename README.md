# Integrators

This app calculates the integration of $f(x)$ from $x_0$ to $x_1$ numerically using three methods:
- The midpoint rule
$$ \frac{df}{dx} = P_0\left(\frac{x_0 + x_1}{2}\right) = f\left(\frac{x_0 + x_1}{2}\right)$$
- The trapezoidal rule
$$ \frac{df}{dx} = P_1\left(\frac{x_0 + x_1}{2}\right) = \frac{f(x_0) + f(x_1)}{2}$$
- Simpson's rule
$$ \frac{df}{dx} = P_2\left(\frac{x_0 + x_1}{2}\right) = \frac{f(x_0) + 4f\left(\frac{x_0 + x_1}{2}\right) + f(x_1)}{6}$$