;; Copyright (c) Goran Jovic, 2011. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns utils)

(defn div [x y]
  (if (zero? y) (Double/NaN) (/ x y)))

(defn l [x y] x)

(defn r [x y] y)


(defn re-replace [string pattern replacement]
    (.replaceAll string (str pattern) (str replacement)))

(def operators-visible {+ "+", - "-", * "*", div "/", r "r", l "l"})

(defn print-rel [e1 rel e2]
          (if (= rel l) e1
          (if (= rel r) e2
              (str "(" e1 " " (operators-visible rel) " " e2 ")" ))))

(defn equation-pretty-print [[numbers operators] value]
      (let [[a b c d e f] numbers
            [o p q r s] operators]
         (str value " = "
         (print-rel (print-rel (print-rel e q d) p a)
                       o (print-rel b r (print-rel c s f))))))
