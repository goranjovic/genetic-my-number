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


(defn sum-form [oldvalues result]
  (html-doc "Genetic My Number"
    (form-to [:post "/"] 
      (text-field {:size 3 :class "x"} :x (oldvalues :x))
      (text-field {:size 3 :class "a"} :a (oldvalues :a)) 
      (text-field {:size 3 :class "b"} :b (oldvalues :b))
      (text-field {:size 3 :class "c"} :c (oldvalues :c))
      (text-field {:size 3 :class "d"} :d (oldvalues :d))
      (text-field {:size 3 :class "e"} :e (oldvalues :e))
      (text-field {:size 3 :class "f"} :f (oldvalues :f))
      (text-area  {:class "result"} :result result) 
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
      (solve x [a b c d e f]))) 

(defroutes webservice
  (GET "/" (sum-form params nil))
  (GET "/*"
       (or (serve-file "./src/main/webapp" (params :*)) 
       :next))
  (GET "*"  404)
  (POST "/" 
    (sum-form params (result (params :x) (params :a) (params :b) (params :c) (params :d) (params :e) (params :f)))))

(defn serve-app []
	(defonce server
	     (run-server {:port 8080 :join? false} 
  	        "/*" (servlet webservice))))
