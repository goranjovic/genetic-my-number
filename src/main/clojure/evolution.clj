(ns evolution)
(use 'clojure.contrib.seq-utils)
(use 'clojure.contrib.math)

(defn div [x y]
	(if (zero? y) (Double/NaN) (/ x y)))

(defn l [x y] x)

(defn r [x y] y)


(defn random-operator []
	( [ + - * div r l ](rand-int 6)))

(defn change-operator [operators index]
	(let [operator (operators index)]
		(replace {operator (random-operator)} operators)))

(defn insert [vect position element]
	(into [] (concat (subvec vect 0 position) [element] (subvec vect (inc position)))))

(defn swap-elements [elements index1 index2]
		(insert (insert elements index1 (elements index2)) index2 (elements index1)))

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

(defn calculate-fitness [goal-value [numbers operators]]
	(let [difference (abs (- goal-value (evaluate [numbers operators])))]
		(if (or (ratio? difference)(Double/isNaN difference))
				 (Double/POSITIVE_INFINITY) difference)))

(defn sort-by-fitness [goal-value population]
	(sort-by (partial calculate-fitness goal-value) population))

(defn select-survivors [population]
		(take (/ (count population) 2) population))

(defn next-generation [survivors]
		(interleave (map mutate survivors) survivors))

(def operators-visible {+ "+", - "-", * "*", div "/", r "r", l "l"})

(defn print-rel [e1 rel e2]
	(if (= rel l) e1 
		(if (= rel r) e2
			(str "(" e1 " " (operators-visible rel) " " e2 ")" ))))


(defn equation-pretty-print [[numbers operators] generation]
		(let [[a b c d e f] numbers [o p q r s] operators]
		(str "[gen: " generation "] " 
				(evaluate [numbers operators]) " = " 
		(print-rel (print-rel (print-rel e q d) p a) o (print-rel b r (print-rel c s f))) )))


(defn evolution [goal-value generation options  old-population]
	(let [sorted-population (sort-by-fitness goal-value  old-population )
	      champion (first (take 1 sorted-population))]
		(if (or (= generation (options :max-gen)) (= (evaluate champion) goal-value))
			(equation-pretty-print champion generation)
			(evolution goal-value (inc generation) options
				(next-generation (select-survivors sorted-population))))))

(def default-options {:population-size 300 :max-gen 1000})

(defn solve
	([goal-value numbers] 
		(solve goal-value numbers default-options)) 
	([goal-value numbers user-options]
		(let [options (merge-with (fn [v1 v2] (if v2 v2 v1)) default-options user-options)]
		(evolution goal-value 0 options 
			(create-initial-population (options :population-size) numbers) ))))
