(ns web
(:use evolution)
(:use compojure))


(defn html-doc 
  [title & body] 
  (html 
    (doctype :html4) 
    [:html 
      [:head 
        [:title title]] 
      [:body 
       [:div 
	[:h2 
	 ;; Pass a map as the first argument to be set as attributes of the element
	 [:a {:href "/"} "Genetic My Number"]]]
        body]])) 


(def sum-form 
  (html-doc "Sum" 
    (form-to [:post "/"] 
      (text-field {:size 3} :x)
      (text-field {:size 3} :a) 
      (text-field {:size 3} :b)
      (text-field {:size 3} :c)
      (text-field {:size 3} :d)
      (text-field {:size 3} :e)
      (text-field {:size 3} :f) 
      (submit-button "Solve")))) 

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
  (GET "/favicon.ico" nil)
  (GET "/" sum-form) 
  (POST "/" 
    (result (params :x) (params :a) (params :b) (params :c) (params :d) (params :e) (params :f)))) 

(defn serve-app []
	(run-server {:port 8080 :join? false} 
  	"/*" (servlet webservice)))
