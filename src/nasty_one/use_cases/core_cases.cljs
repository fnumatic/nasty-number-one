(ns nasty-one.use-cases.core-cases
  (:require [nasty-one.db :as db]
            [re-frame.core :as rf]
            [nasty-one.use-cases.bot :as bot]
            [tools.reframetools :refer [sdb gdb]]))
   ;[day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

(defn calc [{:keys [ round]} dice]
  {:round (if (= dice 1) 0 (+ round dice))})

(defn endrnd [{:keys [total round]}]
  {:total (+ total round)
   :round 0
   :active? false})

(defn isEvil [dice]
  (= dice 1))

(defn next-player [idx]
 (mod (inc idx) 2))

(defn activate [pl]
  (assoc pl :active? true))


(defn endround [{:keys [db]} _]
 (let [current-path [:players (:current-player db)]
       next-path [:players (next-player (:current-player db))]
       player-data (endrnd (get-in db current-path))]
       
   {:db (->  db
             (update :current-player next-player)
             (update-in current-path #(merge % player-data))
             (update-in next-path activate))
    :fx [[:dispatch [:dice/dice-bot]]]}))

(defn dice [{:keys [db]} _]
  (let [v (inc (rand-int 6))
        current-path [:players (:current-player db)]
        player-data (calc (get-in db current-path) v)]
    {:db (-> db
             (assoc :active-dice v)
             (update-in current-path #(merge %  player-data)))
     :fx [(when (isEvil v) [:dispatch [:dice/endround]])]}))

(defn later [t msg]
  [:dispatch-later {:ms t :dispatch msg}])
(defn max-totals [db]
  (->> (:players db)
       (map :total)
       (reduce max)))

(defn dice-bot [{:keys [db]} [_ round]]
  (let [b           (get-in db [:players 0])
        bot?        (= (:current-player db) 0)
        round       (or round 1)
        dice-again? (bot/botPlay round 1 (max-totals db) (:total b))]
    (cond
      (and bot? dice-again?)       {:fx [(later 500 [:dice/dice])
                                         (later 600 [:dice/dice-bot (inc round)])]}
      (and bot? (not dice-again?)) {:fx [(later 500 [:dice/endround])]}
     ;;human player
      :else nil)))

(defn start-bot [_ _]
  {:fx [[:dispatch-later {:ms 1500 :dispatch [:dice/dice-bot]}]]})


(rf/reg-sub ::name (gdb [:name]))
(rf/reg-sub ::active-panel (gdb [:active-panel]))
(rf/reg-sub ::re-pressed-example  (gdb [:re-pressed-example]))

(rf/reg-event-db ::initialize-db (constantly db/default-db))
(rf/reg-event-db ::set-active-panel [rf/debug] (sdb [:active-panel]))


(rf/reg-sub :dice/players (gdb [:players]))
(rf/reg-sub :dice/active-dice (gdb [:active-dice]))

(rf/reg-event-fx :dice/dice dice)
(rf/reg-event-fx :dice/endround endround)

(rf/reg-event-fx :dice/start-bot start-bot)
(rf/reg-event-fx :dice/dice-bot dice-bot)


