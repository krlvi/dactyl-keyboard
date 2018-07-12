;; --- BEGIN AGPLv3-preamble ---
;; Dactyl Marshmallow ergonomic keyboard generator
;; Copyright (C) 2015, 2018 Matthew Adereth and Jared Jennings
;;
;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU Affero General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Affero General Public License for more details.
;;
;; You should have received a copy of the GNU Affero General Public License
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.
;; --- END AGPLv3-preamble ---
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
