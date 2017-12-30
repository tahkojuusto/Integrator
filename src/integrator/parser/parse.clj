(ns integrator.parser.parse
    (:require [integrator.parser.lex :as lex])
    (:require [clojure.tools.logging :as log])
    (:use [integrator.util :only (find-first-grammar-rule if-let*)]))

; Parser that handles infix math expressions.
;
; Even though the grammar is LL(1), the parses uses
; the backtracking algorithm.
;
;
; Context-free LL(1) grammar rules:
;
; start --> expr + start (1) | expr - start (2) | expr (3)
; expr  --> fact * expr  (4) | fact / expr  (5) | fact (6)
; fact  --> (start)      (7) | INTEGER       (8)
;


; Declare start function here as there are cross references between
; functions.
(declare -start)

; A node in a tree describing the abstract syntax tree (AST).
(defrecord TreeNode [value left-node right-node])

(defn -match
    "Take the first token, verify it is a non-terminal
    and matches the expected token type.

    Returns the rest of tokens if there is a match.
    Otherwise, returns false."
    [tokens type]
    (log/trace "ENTERING parse/-match.")
    (if (= (:type (first tokens)) type)
        (rest tokens)
        false))

(defn -fact
    "Apply one of the grammar rules concerning parenthesis and non-terminals:
    fact --> (start) | INTEGER."
    [tokens]
    (log/trace "ENTERING parse/-fact.")
    (find-first-grammar-rule [
                              ; fact --> INTEGER
                              (if-let* [integer-tokens (or (-match tokens "val") (-match tokens "var"))]
                                       [integer-tokens (TreeNode. (first tokens) nil nil)])

                              ; fact --> (start)
                              (if-let* [left-par-tokens (-match tokens "l-par")
                                        [start-tokens start-ast] (-start left-par-tokens)
                                        right-par-tokens (-match start-tokens "r-par")]
                                       [right-par-tokens start-ast])]))

(defn -expr
    "Apply one of the grammar rules concerning operators * and /:
    expr --> fact * expr | fact / expr | fact."
    [tokens]
    (log/trace "ENTERING parse/-expr.")
    (find-first-grammar-rule [
                              ; expr --> fact {*,/} expr
                              (if-let* [[fact1-tokens fact1-ast] (-fact tokens)
                                        operator (if (-match fact1-tokens "op-mult")
                                                     (first fact1-tokens)
                                                     false)
                                        [fact2-tokens fact2-ast] (-expr (rest fact1-tokens))]
                                       [fact2-tokens (TreeNode. operator fact1-ast fact2-ast)])

                              ; expr --> fact
                              (if-let* [[fact-tokens fact-ast] (-fact tokens)]
                                       [fact-tokens fact-ast])]))

(defn -start
    "Apply one of the grammar rules concerning operators + and -:
    start --> expr + start | expr - start | expr."
    [tokens]
    (log/trace "ENTERING parse/-start.")
    (find-first-grammar-rule [
                              ; start --> expr {+,-} start
                              (if-let* [[expr1-tokens expr1-ast] (-expr tokens)
                                        operator (if (-match expr1-tokens "op-add")
                                                     (first expr1-tokens)
                                                     false)
                                        [expr2-tokens expr2-ast] (-start (rest expr1-tokens))]
                                       [expr2-tokens (TreeNode. operator expr1-ast expr2-ast)])

                              ; start --> expr
                              (if-let* [[expr-tokens expr-ast] (-expr tokens)]
                                       [expr-tokens expr-ast])]))

(defn -combine-tree
    "Go through the tree, and form textual Clojure function."
    [ast]
    (log/trace "ENTERING parse/-combine-tree.")
    (cond
          ; Leaf reached. Constant values are evaluated as they are.
          ; Variables are converted to symbols, e.g. "+" --> +.
          (= (:type (:value ast)) "val") (:value (:value ast))
          (= (:type (:value ast)) "var") (symbol (:value (:value ast)))

          ; Node is not a leaf. Create s-expression (op l-val r-val).
          :else (let [op (symbol (:value (:value ast)))
                      left-operand  (-combine-tree (:left-node ast))
                      right-operand (-combine-tree (:right-node ast))]
                    (list (symbol op) left-operand right-operand))))

(defn parse
    "Given tokens from lexical analysis, return the abstract syntax tree (AST)."
    [tokens]
    (log/trace "ENTERING parse/parse.")
    (let [result (-start tokens)]
        (if result
            (if (empty? (first result))
                (second result)
                ; There were non-processed tokens left. This should not be the case
                ; if the syntax is correct.
                (throw (Exception. "ERR: Syntax error: All tokens were not used!")))
            ; Using given grammar rules, no valid solution could be found.
            (throw (Exception. "ERR: Syntax error.")))))

(defn create-fn
    "Given the AST, form Clojure function."
    [ast]
    (log/trace "ENTERING parse/-create-fn.")
    (eval (list 'fn '[x] (-combine-tree ast))))