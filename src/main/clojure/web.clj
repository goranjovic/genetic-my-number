(ns web
(:use evolution)
(:use compojure))


(defn html-doc 
  [title & body] 
  (html 
    (doctype :html4) 
    [:html 
      [:head 
        [:title title]
	(include-css "style.css")]
      [:body 
       [:div 
	[:h2 
	 [:a {:href "/"} title]]]
        body]])) 


(def sum-form 
  (html-doc "Genetic My Number" 
    (form-to [:post "/"] 
      (text-field {:size 3 :class "x"} :x)
      (text-field {:size 3 :class "a"} :a) 
      (text-field {:size 3 :class "b"} :b)
      (text-field {:size 3 :class "c"} :c)
      (text-field {:size 3 :class "d"} :d)
      (text-field {:size 3 :class "e"} :e)
      (text-field {:size 3 :class "f"} :f) 
      (submit-button { :class "solve"} "Solve")))) 

(defn result 
  [x a b c d e f] 
  (let [x (Integer/parseInt x)
	a (Integer/parseInt a)
        b (Integer/parseInt b)
        c (Integer/parseInt c)
        d (Integer/parseInt d)
        e (Integer/parseInt e) 
        f (Integer/parseInt f)] 
    (html-doc "Result" 
      (solve x [a b c d e f])))) 

(defroutes webservice
  (GET "/" sum-form)
  (GET "/*"
       (or (serve-file "./src/main/webapp" (params :*)) 
       :next))
  (GET "*"  404)
  (POST "/" 
    (result (params :x) (params :a) (params :b) (params :c) (params :d) (params :e) (params :f)))) 

(defn serve-app []
	(run-server {:port 8080 :join? false} 
  	"/*" (servlet webservice)))
