;; Copyright (c) Goran Jovic, 2010. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

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
	(include-css "style.css")
        (include-js "ga.js")]
      [:body  
       [:div {:class "title"}
	[:h2 
	 [:p title]]]
        body]]))

(def web-root-path "./src/main/webapp")

(def code-link "http://code.google.com/p/genetic-my-number/")

(def look-ma-link "http://look-ma.appspot.com/")

(def locale (read-properties (str web-root-path "/locale.properties")))

(defn localize [kwd]
	(. locale getProperty (name kwd) (name kwd)))


(defn form-element [oldvalues elem]
	[:div {:class (name elem)} (label (name elem) (localize elem))
	(text-field {:size 3} elem (oldvalues elem))])


(defn form-structure [kwds oldvalues] 
  [:div {:class "form"}
   (map (partial form-element oldvalues) kwds)
    [:div {:class "options"}
     (map (partial form-element {}) [:max-gen :population-size])]])

(defn description-text []
  [:div {:class "description"} 
   [:ul [:li (localize :description)]
        [:li (localize :details) " " 
         [:a {:href code-link :target "_blank"} (localize :code)]]]])

(defn legal-text []
  [:div {:class "footer"} 
   [:div {:class "legal"}(localize :legal)]
   [:div {:class "legal"}(localize :acknowledgement) 
    [:a {:href look-ma-link :target "_blank"} "nevenavv"]]])

(defn sum-form [oldvalues result]
  (html-doc (localize :title) 
    (form-to [:post "/"]
      (form-structure [:goal :a :b :c :d :e :f] oldvalues)
      (text-area  {:class "result" :readonly "true"} :result result) 
      (submit-button { :class "solve"} (localize :solve))
      (description-text)
      (legal-text))))

(defn parse-int 
      ([raw default]
	(if (or (nil? raw) (= raw ""))
		default (Integer/parseInt raw)))
      ([raw](parse-int raw nil)))

(defn result 
  [params]
  (let [x (parse-int (params :goal) 0)
	a (parse-int (params :a) 0)
        b (parse-int (params :b) 0)
        c (parse-int (params :c) 0)
        d (parse-int (params :d) 0)
        e (parse-int (params :e) 0) 
        f (parse-int (params :f) 0)
	
	max-gen (parse-int (params :max-gen))
	population-size (parse-int (params :population-size))
	] 
      (solve x [a b c d e f] {:max-gen max-gen :population-size population-size}))) 

(defn service-output [params]
  (apply str (interpose ";" (result params))))

(defroutes webservice
  (GET "/service" (service-output params))
  (GET "/" (sum-form params nil))
  (GET "/*"
       (or (serve-file web-root-path (params :*)) 
       :next))
  (GET "*"  404)
  (POST "/" 
    (sum-form params (first (result params)))))

(defn serve-app []
	(defonce server
	     (run-server {:port 8080 :join? false} 
  	        "/*" (servlet webservice))))
