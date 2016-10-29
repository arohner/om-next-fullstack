(ns todomvc.todomvc
  (:require #?@(:cljs [[goog.events :as events]
                       [goog.dom :as gdom]])
            [om.next :as om :refer [defui]]
            [om.dom :as dom]
            [todomvc.util :as util :refer [hidden pluralize]]
            [todomvc.item :as item]
            [todomvc.client-parser :as p])
  #?(:cljs (:import [goog History]
                    [goog.history EventType])))

;; -----------------------------------------------------------------------------
;; Components

#?(:cljs (enable-console-print!))

(defui Header
  static om/Ident
  (ident [this {:keys [app/user] :as props}]
      (println "header ident:" props)
      [:app/user user])
  static om/IQuery
  (query [this]
      ['[:app/user _]])
  Object
  (render [this]
      (println "header render" (om/props this))
      (let [props (om/props this)]
        (dom/div nil
                 (str "current-user:" (:app/user props))))))

(def header (om/factory Header))

(defui Main
  static om/IQuery
  (query [this]
      `[:app/headline :app/user {:header/data ~(om/get-query Header)}])
  Object
  (render [this]
      (println "main render" (om/props this))
      (let [{:keys [app/headline]} (om/props this)]

        (dom/div nil
                 (str "Hello, " headline)
                 (header (:header (om/props this)))))))

#?(:clj
   (defn make-reconciler [conn]
     (om/reconciler
       {:state     (atom {})
        :normalize true
        :parser    (om/parser {:read p/read :mutate p/mutate})
        :send      (util/server-send conn)})))

#?(:cljs
   (def reconciler
     (om/reconciler
       {:state     (atom {})
        :normalize true
        :parser    p/parser
        :send      (util/transit-post "/api")})))

#?(:cljs (om/add-root! reconciler Main (gdom/getElement "todoapp")))
