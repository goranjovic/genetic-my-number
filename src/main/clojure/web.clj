(ns web
(:use evolution)
(:use compojure)
(:use clojure.contrib.java-utils))


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
	 [:p title]]]
        body]]))

(def web-root-path "./src/main/webapp")

(def locale (read-properties (str web-root-path "/locale.properties")))

(defn localize [kwd]
	(. locale getProperty (name kwd) (name kwd)))

(defn form-element [oldvalues elem]
     (if (vector? elem)
	(form-structure elem oldvalues)
	[:div {:class (name elem)} (label (name elem) (localize elem))
	(text-field {:size 3} elem (oldvalues elem))]))

(defn form-structure [kwds oldvalues]
		[:div {:class ((meta kwds) :div-id)}
		(map (partial form-element oldvalues)
		kwds )])

(defn sum-form [oldvalues result]
  (html-doc "Genetic My Number"
    (form-to [:post "/"]
      (form-structure
		(with-meta [:goal :a :b :c :d :e :f 
			(with-meta [:max-gen :population-size]{:div-id "options"})] 
			{:div-id "form"}) oldvalues)
      (text-area  {:class "result"} :result result) 
      (reset-button { :class "reset"} (localize :reset))
      (submit-button { :class "solve"} (localize :solve)))))

(defn parse-int [raw]
	(if (or (nil? raw) (= raw ""))
		nil (Integer/parseInt raw)))

(defn result 
  [params]
  (let [x (parse-int (params :goal))
	a (parse-int (params :a))
        b (parse-int (params :b))
        c (parse-int (params :c))
        d (parse-int (params :d))
        e (parse-int (params :e)) 
        f (parse-int (params :f))
	
	max-gen (parse-int (params :max-gen))
	population-size (parse-int (params :population-size))
	] 
      (solve x [a b c d e f] {:max-gen max-gen :population-size population-size}))) 

(defroutes webservice
  (GET "/" (sum-form params nil))
  (GET "/*"
       (or (serve-file web-root-path (params :*)) 
       :next))
  (GET "*"  404)
  (POST "/" 
    (sum-form params (result params))))

(defn serve-app []
	(defonce server
	     (run-server {:port 8080 :join? false} 
  	        "/*" (servlet webservice))))
