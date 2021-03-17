(ns nasty-one.use-cases.bot)

(def decisionValue 0)
(def max-val 30)
(def min-val 10)
(def middle (- max-val min-val))

(defn roundRisk [playedRound val]
  (if (= playedRound 1)
    (+ val max-val)
    (->> (- (* playedRound 2.5) 2.5)
         (- max-val)
         (+ val))))

(defn lvl
  [level val]
  (condp = level 
    0 (+ val max-val)
    1 (+ val middle)
      (+ val min-val)))
(defn mean [coll]
  (let [sum (apply + coll)
        count (count coll)]
    (if (pos? count)
      (/ sum count)
      0)))
(defn median [coll]
  (let [sorted (sort coll)
        cnt (count sorted)
        halfway (quot cnt 2)]
    (if (odd? cnt)
      (nth sorted halfway) ; (1)
      (let [bottom (dec halfway)
            bottom-val (nth sorted bottom)
            top-val (nth sorted halfway)]
        (mean [bottom-val top-val])))))
(defn kiWinOrLose
  [ply1 ply2 val]
  (let [winNumber       200
        hlf-min-val     (/ min-val 2)
        relval          #(/ (* 100 %) winNumber)
        median-ap       (/ (* max-val (median [(relval ply1) (relval ply2)])) 100)
        player1wins     (> ply1 ply2)
        needPoints      (< (+ median-ap hlf-min-val) min-val)
        enoughPoints    (< (- median-ap hlf-min-val) min-val)]
    (cond 
      (and player1wins needPoints)         (+ val min-val)
      (and player1wins (not needPoints))   (+ (+ val median-ap) hlf-min-val)
      (and (not player1wins) enoughPoints) (+ val min-val)
      :else                                (- (+ val median-ap) hlf-min-val))))

(defn playedRoundPointsRisk [playedRound val]
  (let [delta (- max-val (* (/ middle 7) (- playedRound 1)))]
    (if (<= playedRound 8) 
      (+ val delta) 
      val)))

(defn playOrNot [decVal4]
  (> decVal4 80))

(defn cutAtRound [maxRound round param] 
  (if  (<= round maxRound) 
    param
    false))

(defn botPlay [round level plyMax plyBot]
  (->> 0
       (roundRisk round)
       (lvl level)
       (kiWinOrLose plyMax plyBot)
       (playedRoundPointsRisk round)
       playOrNot
       (cutAtRound 8 round)))



(comment
  (botPlay  3, 1, 110, 178)
  ,)
  