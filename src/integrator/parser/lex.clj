(ns integrator.parser.lex)

(def operators     #{\+ \- \* \/})
(def left-bracket  \()
(def right-bracket \))
(def variable      \x)

(defn token [type value] {:type type :value value})

(defn left-bracket-token []   (token "l-par" "("))
(defn right-bracket-token []  (token "r-par" ")"))
(defn operator-token [val]    (token "op" val))
(defn variable-token [val]    (token "var" val))
(defn number-token [val]      (token "val" val))

(defn is-whitespace? [c]    (clojure.string/blank? (str c)))
(defn is-left-bracket? [c]  (= left-bracket c))
(defn is-right-bracket? [c] (= right-bracket c))
(defn is-operator? [c]      (contains? operators c))
(defn is-variable? [c]      (= variable c))
(defn is-number? [c]        (Character/isDigit c))

(defn -scan [input-str peek tokens]
    (let [c    (first input-str) ; first char in the input string.
          peek (str peek c)] ; buffered first chars comprising a part of token value.
        (cond
              ; Input string is empty, the whole expression is read.
              ; Return all found tokens.
              (not c)               tokens

              ; Continue with the input string if there are whitespaces.
              (is-whitespace? c)    (-scan (rest input-str) "" tokens)

              ; Detect brackets.
              (is-left-bracket? c)  (-scan (rest input-str) "" (conj tokens (left-bracket-token)))
              (is-right-bracket? c) (-scan (rest input-str) "" (conj tokens (right-bracket-token)))

              ; Detect operators.
              (is-operator? c)      (-scan (rest input-str) "" (conj tokens (operator-token peek)))

              ; Detect variable 'x'.
              (is-variable? c)      (-scan (rest input-str) "" (conj tokens (variable-token peek)))

              ; Detect integers.
              (is-number? c)        (let [second-c (second input-str)]
                                        ; Push token if the second next char is not a digit.
                                        ; Otherwise, continue buffering first chars.
                                        (if (not (and second-c (is-number? second-c)))
                                            (-scan (rest input-str) "" (conj tokens (number-token (Integer/parseInt peek))))
                                            (-scan (rest input-str) peek tokens)))

              ; No valid symbol, stop the lexer.
              :else (throw (Exception. (str "Unknown token: " peek))))))

(defn scan [input-str]
    (-scan input-str "" []))