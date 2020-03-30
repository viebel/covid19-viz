(ns covid19-viz.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [re-pressed.core :as rp]
   [covid19-viz.events :as events]
   [covid19-viz.routes :as routes]
   [covid19-viz.views :as views]
   [covid19-viz.config :as config]
   ))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn init []
  (routes/app-routes)
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::rp/add-keyboard-event-listener "keydown"])
  (dev-setup)
  (mount-root))
