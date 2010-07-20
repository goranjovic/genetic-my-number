(ns evolution)
(use 'clojure.contrib.seq-utils)
(use 'clojure.contrib.math)

(defn div [x y]
	(if (zero? y) x (/ x y)))

(defn l [x y] x)

(defn r [x y] y)


(defn random-operator []
	( [ + - * div r l ](rand-int 6)))

(defn change-operator [operators index]
	(let [operator (operators index)]
		(replace {operator (random-operator)} operators)))

(defn swap-elements [elements index1 index2]
	(let [element1 (elements index1) element2 (elements index2)]
		(replace {element1 element2 element2 element1} elements)))

(defn create-rand-operators [size]
        (take size (repeatedly random-operator)))

(defn create-rand-equation [numbers]
	[(vec (shuffle numbers)) (vec (create-rand-operators (dec (count numbers))))])

(defn mutate [[numbers operators]]
        (let [toss-coin (rand-int 2)]
                (if (= toss-coin 0) 
                        [numbers (change-operator operators (rand-int 5))]
                        [(swap-elements numbers (rand-int 6) (rand-int 6)) operators])))

(defn evaluate [[numbers operators]]
        (let [[a b c d e f] numbers [o p q r s] operators]
        (o (p (q e d) a) (r b (s c f))) ))


(defn create-initial-population [population-size numbers]
	(take population-size (repeatedly (partial create-rand-equation numbers))))

(defn calculate-fitness [goal-value [numbers operators] ]
	(with-meta [numbers operators] {:fitness (abs (- goal-value (evaluate [numbers operators])))}))
