(ns integrator.util)

(defmacro if-let*
    "Multiple binding version of if-let
    TODO: Refactor this."
    ([bindings then]
     `(if-let* ~bindings ~then nil))
    ([bindings then else]
     (if (seq bindings)
         `(if-let [~(first bindings) ~(second bindings)]
              (if-let* ~(vec (drop 2 bindings)) ~then ~else)
              ~else)
         then)))

(defn find-first-grammar-rule
    "Given a set of vectors, return the first satisfying the string input.
    More generally, return the first item, which is not null."
    [rules]
    (first (filter #((comp not nil?) %) rules)))