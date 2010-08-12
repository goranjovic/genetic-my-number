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
       [:div {:class "title"}
	[:h2 
	 [:a {:href "/" } title]]]
        body]])) 


(defn sum-form [oldvalues result]
  (html-doc "Genetic My Number"
    (form-to [:post "/"]
      (map (fn [kwd] 
		(text-field {:size 3 :class (name kwd)} kwd (oldvalues kwd))) 
		[:x :a :b :c :d :e :f]) 
      (text-area  {:class "result"} :result result) 
      (submit-button { :class "solve"} "Solve"))))

(defn result 
  [params]
  (let [x (Integer/parseInt (params :x))
	a (Integer/parseInt (params :a))
        b (Integer/parseInt (params :b))
        c (Integer/parseInt (params :c))
        d (Integer/parseInt (params :d))
        e (Integer/parseInt (params :e)) 
        f (Integer/parseInt (params :f))] 
      (solve x [a b c d e f]))) 

(defroutes webservice
  (GET "/" (sum-form params nil))
  (GET "/*"
       (or (serve-file "./src/main/webapp" (params :*)) 
       :next))
  (GET "*"  404)
  (POST "/" 
    (sum-form params (result params))))

(defn serve-app []
	(defonce server
	     (run-server {:port 8080 :join? false} 
  	        "/*" (servlet webservice))))
