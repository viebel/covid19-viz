(ns covid19-viz.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core :as re-com]
   [covid19-viz.subs :as subs]
   [covid19-viz.events :as events]
   ["react-chartjs-2" :refer [Line]]))


;; home

(defn display-re-pressed-example []
  (let [re-pressed-example (re-frame/subscribe [::subs/re-pressed-example])]
    [:div

     [:p
      [:span "Re-pressed is listening for keydown events. A message will be displayed when you type "]
      [:strong [:code "hello"]]
      [:span ". So go ahead, try it out!"]]

     (when-let [rpe @re-pressed-example]
       [re-com/alert-box
        :alert-type :info
        :body rpe])]))

(defn home-title []
  (let [name (re-frame/subscribe [::subs/name])]
    [re-com/title
     :label (str "Hello from " @name ". This is the Home Page.")
     :level :level1]))

(defn link-to-about-page []
  [re-com/hyperlink-href
   :label "go to About Page"
   :href "#/about"])

(defn home-panel []
  [re-com/v-box
   :gap "1em"
   :children [[home-title]
              [link-to-about-page]
              [display-re-pressed-example]
              ]])

(defn avg [& x]
  (let [y (remove nil? x)]
    (* 1.0
       (/ (apply + y)
          (count y)))))

(defn running-avg [days data]
  (concat [nil]
          (apply map avg
                 (concat [data]
                         (map #(concat (repeat % nil) data) (range 1 days))))))

(defn delta [x]
  (concat [nil]
          (map - (rest x)
               x)))

(defn safe-divide [a b]
  (if (zero? b)
    0
    (float (/ a b))))

(defn relative-growth [days data]
  (concat (repeat days 0)
          (map safe-divide (nth (iterate rest data) days) data)))

(defn country-table [country data]
  [re-com/v-box
   :children [[:h3 country]
              [re-com/v-box
               :gap "2em"
               :children [[:> Line {:data {:labels (map :date data)
                                           :datasets [{:label "Confirmed cases"
                                                       :data (map :confirmed data)
                                                       :borderColor "orange"}
                                                      {:label "Active cases"
                                                       :data (map :active data)
                                                       :borderColor "red"}]}}]
                          [:> Line {:data {:labels (map :date data)
                                             :datasets [{:label "Death cases"
                                                         :data (map :deaths data)
                                                         :borderColor "red"}]}}]
                          [:> Line {:data {:labels (map :date data)
                                           :datasets [{:label "Confirmed cases - Delta"
                                                       :data (delta (map :confirmed data))
                                                       :borderColor "blue"}
                                                      {:label "Confirmed cases - Delta 3 days average"
                                                       :data (running-avg 3 (delta (map :confirmed data)))
                                                       :borderColor "orange"}
                                                      {:label "Confirmed cases - Delta 7 days average"
                                                       :data (running-avg 7 (delta (map :confirmed data)))
                                                       :borderColor "green"}]}}]
                          [:> Line {:data {:labels (map :date data)
                                           :datasets [{:label "Confirmed cases - 3 days relative growth"
                                                       :data (relative-growth 3 (map :confirmed data))
                                                       :borderColor "blue"}
                                                      {:label "Confirmed cases - 3 days relative growth 3 days average"
                                                       :data (running-avg 7 (relative-growth 3 (map :confirmed data)))
                                                       :borderColor "green"}]}}]
                          [:> Line {:height 500
                                      :width 1000
                                    :data {:labels (map :date data)
                                           :datasets [{:label "Active cases - Delta"
                                                       :data (delta (map :active data))
                                                       :borderColor "blue"}
                                                      {:label "Active cases - Delta 3 days average"
                                                       :data (running-avg 3 (delta (map :active data)))
                                                       :borderColor "orange"}
                                                      {:label "Active cases - Delta 7 days average"
                                                       :data (running-avg 7 (delta (map :active data)))
                                                       :borderColor "green"}]}}]
                          #_[:> Line {:height 500
                                      :width 1000
                                      :data {:labels (map :date data)
                                           :datasets [{:label "Deaths"
                                                       :data (map :deaths data)
                                                       :borderColor "red"}]}}]]]]])

(defn select-country-ui []
  (let [countries @(re-frame/subscribe [::subs/covid19-countries])
        country @(re-frame/subscribe [::subs/selected-country])]
    [re-com/v-box
     :children
     [[:div "Select a country"]
      [re-com/single-dropdown
       :width "200px"
       :choices (map (fn [c] {:id c :label (name c)})
                     countries)
       :model country
       :filter-box? true
       :placeholder "Choose a country"
       :on-change #(re-frame/dispatch [::events/select-country %])]]]))

(defn covid19-table []
  (let [data @(re-frame/subscribe [::subs/covid19-data])
        selected-country @(re-frame/subscribe [::subs/selected-country])]
    [re-com/v-box
     :children [[:h2 "Charts"]
                [re-com/h-box
                 :children [[country-table selected-country (get data selected-country)]]]]]))

(defn covid19-all-countries []
  (let [data @(re-frame/subscribe [::subs/covid19-data-all])]
    [re-com/v-box
     :children [[:h2 "The Overall"]
                [country-table "Overall" data]]]))

(defn covid19-data-ui []
  [re-com/v-box
   :gap "1em"
   :children [[covid19-all-countries]
              [select-country-ui]
              [covid19-table]]])

(defn main-panel []
  [re-com/v-box
   :height "100%"
   :children [[covid19-data-ui]]])
