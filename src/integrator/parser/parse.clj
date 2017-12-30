(ns integrator.parser.parse
    (:require [integrator.parser.lex :as lex])
    (:use [integrator.util :only (find-first-rule if-let*)]))

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
    "Takes the first token, verifies it is a non-terminal
    and matches the expected token type.

    Returns the rest of tokens if there is a match.
    Otherwise, returns false."
    [tokens type]
    (if (= (:type (first tokens)) type)
        (rest tokens)
        false))

(defn -fact
    "Applies one of the grammar rules concerning parenthesis and non-terminals:
    fact --> (start) | INTEGER."
    [tokens]
    (find-first-rule [
                      ; fact --> INTEGER
                      (if-let* [integer-tokens (-match tokens "val")]
                                [integer-tokens (TreeNode. (first tokens) nil nil)])

                      ; fact --> (start)
                      (if-let* [left-par-tokens (-match tokens "l-par")
                                 [start-tokens start-ast] (-start left-par-tokens)
                                 right-par-tokens (-match start-tokens "r-par")]
                                [right-par-tokens start-ast])]))

(defn -expr
    "Applies one of the grammar rules concerning operators * and /:
    expr --> fact * expr | fact / expr | fact."
    [tokens]
    (find-first-rule [
                      ; expr --> fact {*,/} expr
                      (if-let* [[fact1-tokens fact1-ast] (-fact tokens)
                                 operator (if (-match fact1-tokens "op")
                                              (first fact1-tokens)
                                              false)
                                 [fact2-tokens fact2-ast] (-expr (rest fact1-tokens))]
                                [fact2-tokens (TreeNode. operator fact1-ast fact2-ast)])

                      ; expr --> fact
                      (if-let* [[fact-tokens fact-ast] (-fact tokens)]
                                [fact-tokens fact-ast])]))

(defn -start
    "Applies one of the grammar rules concerning operators + and -:
    start --> expr + start | expr - start | expr."
    [tokens]
    (find-first-rule [
                      ; start --> expr {+,-} start
                      (if-let* [[expr1-tokens expr1-ast] (-expr tokens)
                                 operator (if (-match expr1-tokens "op")
                                              (first expr1-tokens)
                                              false)
                                 [expr2-tokens expr2-ast] (-start (rest expr1-tokens))]
                                [expr2-tokens (TreeNode. operator expr1-ast expr2-ast)])

                      ; start --> expr
                      (if-let* [[expr-tokens expr-ast] (-expr tokens)]
                                [expr-tokens expr-ast])]))

(defn parse
    "Given tokens from lexical analysis, returns the abstract syntax tree (AST)."
    [tokens]
    (let [result (-start tokens)]
        (if result
            (if (empty? (first result))
                (second result)
                ; There were non-processed tokens left. This should not be the case
                ; if the syntax is correct.
                (throw (Exception. "ERR: Syntax error: All tokens were not used!")))
            ; Using given grammar rules, no valid solution could be found.
            (throw (Exception. "ERR: Syntax error.")))))