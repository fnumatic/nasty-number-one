(ns nasty-one.db)

(def default-db
  {:max-value 200
   :current-player 0
   :players [
                  {:name "Bot" :active? true :round 0 :total 0}
                  {:name "Player" :active? false :round 0 :total 0}
   ]})
