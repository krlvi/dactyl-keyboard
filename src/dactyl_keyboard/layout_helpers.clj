(ns dactyl-keyboard.layout-helpers
  (:refer-clojure :exclude [use import])
  (:require [unicode-math.core :refer :all]))

(defn y-and-b-key-silo-widenings [columns rows col row]
  (let [bottom (fn [col]
                 (cond
                   (<= col (first columns)) [0 0]
                   (< col 2) [1 0]
                   (< col 3) [1 1]
                   (< col (last columns)) [0 1]
                   :else [0 0]))
        top (fn [col]
              (cond
                (<= col (first columns)) [0 1]
                (< col 2) [0 1]
                (< col 3) [0 0]
                (< col (last columns)) [1 0]
                :else [1 0]))
        linear (fn [col row]
                 (let [[bl br] (bottom col)
                       [tl tr] (top col)
                       row-amount (/ (- row (first rows)) (- (last rows) (first rows)))
                       left-widening (+ (* bl row-amount) (* tl (- 1 row-amount)))
                       right-widening (+ (* br row-amount) (* tr (- 1 row-amount)))]
                   [left-widening right-widening]))
        step (fn [col row]
               ; row numbers go up as you move from the top toward the thumb
               (if (some #{row} (drop (/ (count rows) 2) rows))
                 (bottom col)
                 (top col)))]
    (step col row)))
