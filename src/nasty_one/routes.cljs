(ns nasty-one.routes
  (:require
    [re-frame.core :as rf]
    [reitit.frontend :as rtf]
    [reitit.frontend.easy :as rtfe]
    [reitit.coercion.schema :as rsc]
    [nasty-one.use-cases.core-cases :as ccases]
    [nasty-one.views.home :as home]
    ))

;;https://clojure.org/guides/weird_characters#__code_code_var_quote
(def routes
    (rtf/router
      ["/"
       [""
        {:name :routes/frontpage
         :view #'home/main}]
       ]

      {:data {:coercion rsc/coercion}}))



(defn app-routes []

  (rtfe/start! routes
               (fn [m] (rf/dispatch [::ccases/set-active-panel m]))
               {:use-fragment true}))



