(ns covid19-viz.views
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :as r :refer [adapt-react-class]]
   [re-com.core :as re-com]
   [covid19-viz.subs :as subs]
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

(defn country-table [country data]
  [re-com/v-box
   :children [[:div country]
              [re-com/v-box
               :gap "2em"
               :children [[:> Line {:data {:labels (map :date data)
                                           :datasets [{:label "Confirmed cases"
                                                       :data (map :confirmed data)
                                                       :borderColor "orange"}]}}]
                          [:> Line {:data {:labels (map :date data)
                                           :datasets [{:label "Deaths"
                                                       :data (map :deaths data)
                                                       :borderColor "red"}]}}]]]]])

(defn covid19-table []
  (let [data (re-frame/subscribe [::subs/covid19-data])]
    [:div
     [:h2 "Table"]
     [:div (for [[country country-data] @data]
             [country-table country country-data])]]))
;; about

(defn about-title []
  [re-com/title
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [re-com/hyperlink-href
   :label "go to Home Page"
   :href "#/"])

(defn about-panel []
  [re-com/v-box
   :gap "1em"
   :children [[about-title]
              [covid19-table]
              [link-to-home-page]]])


;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :about-panel [about-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [re-com/v-box
     :height "100%"
     :children [[panels @active-panel]]]))
