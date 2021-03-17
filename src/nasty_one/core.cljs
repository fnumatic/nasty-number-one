(ns ^:figwheel-hooks nasty-one.core
  (:require
   [reagent.dom :refer [render]]
   [re-frame.core :as re-frame]
   [nasty-one.use-cases.core-cases :as ccases]
   [nasty-one.routes :as routes]
   [nasty-one.views.home :as views]
   [nasty-one.config :as config]
   [nasty-one.styles :as styl]))



(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    ))

(defn mount-root []
  (println "mount")
  (re-frame/clear-subscription-cache!)
  (re-frame/dispatch [:dice/dice-bot])

  (styl/inject-trace-styles js/document)
  (render [views/main-panel]
          (.getElementById js/document "app")))

(defn ^:after-load re-render []
  (mount-root)
  )

(defn ^:export init []
  (re-frame/dispatch-sync [::ccases/initialize-db])
  (dev-setup)
  (routes/app-routes)
  (mount-root)
  )

;(defonce init-block (init))
