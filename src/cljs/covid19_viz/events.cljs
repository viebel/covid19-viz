(ns covid19-viz.events
  (:require
   [re-frame.core :as re-frame]
   [covid19-viz.db :as db]
   [day8.re-frame.http-fx]
   [ajax.core :as ajax]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

(re-frame/reg-event-fx
 ::load-covid19-data
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :get
                 :uri             "https://pomber.github.io/covid19/timeseries.json"
                 :timeout         8000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::load-covid19-data-success]
                 :on-failure      [::load-covid19-data-failure]}}))

(re-frame/reg-event-db
 ::load-covid19-data-success
 (fn [db [_ result]]
   (assoc db :covid19-data result)))

(re-frame/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(re-frame/reg-event-db
 ::set-re-pressed-example
 (fn [db [_ value]]
   (assoc db :re-pressed-example value)))
