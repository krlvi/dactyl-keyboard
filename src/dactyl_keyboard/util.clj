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
(ns dactyl-keyboard.util
  (:refer-clojure :exclude [use import])
  (:require [unicode-math.core :refer :all]
            [scad-clj.model :refer :all]))

(defn triangle-hulls [& shapes]
  (apply union
         (map (partial apply hull)
              (partition 3 1 shapes))))

(defn hull-pairs [& shapes]
  (apply union
         (for [[this next] (map vector shapes (drop 1 shapes))]
           (hull this next))))

(defn hull-a-grid [rows-of-columns]
  "Hull a grid of shapes together. If some of the shapes are nil, that is fine."
  (if (not (empty? (drop 1 rows-of-columns)))
    (apply union
         (for [[row next-row] (map vector rows-of-columns
                                   (drop 1 rows-of-columns))]
           (for [[column next-column column-in-next-row
                  next-column-in-next-row]
                 (map vector row (drop 1 row) next-row (drop 1 next-row))]
             (union
              (hull column next-column column-in-next-row)
              (hull next-column next-column-in-next-row column-in-next-row)))))
    (apply union
           (for [row rows-of-columns]
             (apply hull-pairs row)))))

(defn downward-shadow [height p]
  (->> (project p)
       (extrude-linear {:height height :twist 0 :convexity 0})
       (translate [0 0 (/ height 2)])))

(defn downward-shadow-hull [p]
  (hull p (downward-shadow 1 p)))

; https://tauday.com
(def ^:const τ (* π 2))

; csg food
(def ^:const ε 0.001)

(def ^:const √2 (Math/sqrt 2))

