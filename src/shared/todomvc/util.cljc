(ns todomvc.util
  (:require [cognitect.transit :as t]
            [om.next :as om]
            #?@(:clj [[todomvc.parser :as server-parser]]))
  #?(:cljs (:import [goog.net XhrIo])))

(defn hidden [is-hidden]
  (if is-hidden
    #js {:display "none"}
    #js {}))

(defn pluralize [n word]
  (if (== n 1)
    word
    (str word "s")))

#?(:cljs
   (defn transit-post [url]
     (fn [{:keys [remote]} cb]
       (println "send:" remote)
       (.send XhrIo url
         (fn [e]
           (this-as this
             (let [resp (t/read (t/reader :json) (.getResponseText this))]
               (println "send resp:" resp)
               (cb resp))))
         "POST" (t/write (t/writer :json) remote)
         #js {"Content-Type" "application/transit+json"}))))

#?(:clj
   (def parser
     (om/parser {:read server-parser/readf :mutate server-parser/mutatef})))

#?(:clj
   (defn server-send [conn]
     (fn [{:keys [remote]} cb]
       (let [res (parser {:conn conn} remote)]
         (cb res remote)))))

(comment
  (def sel [{:todos/list [:db/id :todo/title :todo/completed :todo/created]}])

  (t/write (t/writer :json) sel)
  )
