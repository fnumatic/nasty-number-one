(ns nasty-one.views.home
  (:require
    [re-frame.core :as rf]
    [reagent.core :as r]
    [nasty-one.use-cases.core-cases :as ccases]
    ))


(defn tw
  "nested coll of tw classes to :class map
   [:foo [:bar :buzz]] -> {:class [:foo :bar :buzz]}"
  [& classes]
  {:class (flatten (apply concat classes))})


(defn twon
  "tailwind on flag"
  [flag coll]
  (tw
   (if flag
     coll
     (first coll))))

(def symbols
   {1 "⚀"
    2 "⚁"
    3 "⚂"
    4 "⚃"
    5 "⚄"
    6 "⚅"})

(def css
  {:btn [:focus:outline-none :shadow-md :text-2xl :border-2 :p-1 :border-blue-900 :bg-gray-300 :rounded-lg]
   :cnt [:container :mx-auto :px-5 :bg-gray-200]
   :number [:inline-block :text-3xl :w-10 :h-10 :border :border-gray-400 :rounded-full :bg-red-400]
   :header [:text-2xl :font-semibold :pt-2 :pb-4 :text-center :text-gray-700]
   :big-panel [:flex  :space-x-1 :items-stretch]})

(defn player-stats [{:keys [name active? round total]}]
  (let [ cl-player  (twon active? [[:text-center :p-2] :bg-green-600])]
   [:div (tw [:w-full :text-center :p-2])
    [:div cl-player name]
    [:div (tw [:text-center :p-2]) round]
    [:div (tw [:text-center :p-2]) total]]))

(defn stats []
  (r/with-let [players (rf/subscribe [:dice/players])
               max-totals (rf/subscribe [:dice/max-player-value])
               max-value (rf/subscribe [:dice/max-value])
               winner (rf/subscribe [:dice/winner])]
   [:div (tw [:mt-4])
    [:h3 (tw [:text-center :font-semibold :text-gray-700 :px-10])
     "Score"  ]
    [:div (tw [:flex :space-x-2 :p-1])
     (for [[idx pl] (map-indexed vector @players)]
      ^{:key idx} [player-stats pl])]
    [:div (tw [:text-center])
     (if (<= @max-value @max-totals)
       [:span (tw [:font-semibold :bg-red-400 :p-2])
        (str "Winner is " (:name @winner) " with " (:total @winner) " points")]
       [:span (tw [:font-semibold]) (str @max-totals " / " @max-value)])
     ]
    ]))

(defn action-panel []
  [:div  (tw [:flex :flex-col :space-y-2 :p-3 :flex-1 ])
   [:button
    (merge (tw (:btn css))
           {:on-click #(rf/dispatch [:dice/dice])})
    "Dice"] ;;"🎲"
   [:button
    (merge (tw (:btn css))
           {:on-click #(rf/dispatch [:dice/endround])})
    "Next"]]) ;;"⏭"

(defn dicer-ui []
  (r/with-let [dice (rf/subscribe [:dice/active-dice])]
   [:div  (tw [:flex :items-center :justify-center :flex-1 ])
    [:div (tw [:text-7xl :text-gray-700]) (get symbols @dice)]]))

(defn main []
  (r/with-let []
   [:div  (tw (:cnt css))
    [:h2  (tw (:header css) ) "The nasty number " 
     [:span (tw (:number css)) "1"]]
    [:div  (tw (:big-panel css))
     [dicer-ui]
     [action-panel]]
    [stats]
    ]))

;; main

(defn show-panel [route]
  (when-let [route-data (:data route)]
    (let [view (:view route-data)]
      [:<>
       [view]])))
       

(defn main-panel []
  (let [active-route (rf/subscribe [::ccases/active-panel])]
    [:div
     [show-panel @active-route]]))
