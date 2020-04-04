(ns covid19-viz.subs
  (:require
   [gadjett.collections :refer [map-object]]
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 ::selected-country
 (fn [db _]
   (:selected-country (:ui db))))

(defn enrich-country-data-day [{:keys [confirmed deaths recovered] :as data}]
  (assoc data :active (- confirmed deaths recovered)))

(defn enrich-country-data [data]
  (map enrich-country-data-day data))

(defn enrich-data [data]
  (map-object enrich-country-data data))

(defn dates [data]
  (->> data
       first
       val
       first
       (map :date)))

(defn sum-fields [fields coll]
  (into {:date (:date (first coll))}
        (for [field fields]
          [field (apply + (map field coll))])))

(defn sum-countries [data]
  (->> (vals data)
       (apply concat)
       (group-by :date)
       vals
       (map (partial sum-fields [:confirmed :active :deaths :recovered]))
       (sort-by (comp js/Date.parse :date))))

(re-frame/reg-sub
 ::covid19-data
 (fn [db _]
   (enrich-data (:covid19-data db))))

(re-frame/reg-sub
 ::covid19-data-all
 (fn [db _]
   (sum-countries (enrich-data (:covid19-data db)))))

(re-frame/reg-sub
 ::covid19-countries
 (fn [db _]
   (sort (keys (:covid19-data db)))))

(re-frame/reg-sub
 ::re-pressed-example
 (fn [db _]
   (:re-pressed-example db)))
