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
(ns dactyl-keyboard.minktest
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.half-circle-connectors :refer :all]))

(def funky-shape-width 30)
(def funky-shape-depth 20)
(def funky-shape-height 54) ; must > twice outer-gasket-radius
(defn funky-shape [shrink]
  (let [shrink-factor (/ (- 100 shrink) 100)
        fsw (* funky-shape-width shrink-factor)
        fsd (* funky-shape-depth shrink-factor)
        fsh (* funky-shape-height shrink-factor)]
  (cube fsw fsd fsh)))

(def ribbon (intersection
             (difference (funky-shape 0) (funky-shape 0.01))
             (cube 400 400 0.2)))

(def gasket-shell-thickness 1)
(def outer-gasket-radius 20)
(def gasket-shell-radius (- outer-gasket-radius gasket-shell-thickness))

(defn gasket-shape [radius]
  (let [diameter (* 2 radius)]
    (binding [*fn* 24] (sphere radius))))

(def gasket (minkowski ribbon (gasket-shape outer-gasket-radius)))

(def gasket-shell
  (let [little-gasket (minkowski ribbon (gasket-shape gasket-shell-radius))]
    (difference
     (difference gasket little-gasket)
     (funky-shape 0))))
(def gasket-with-joints-pieces
  (pieces-with-x-pins-and-holes
   gasket-shell-radius
   [(fn [shape] (translate [0 (* 1/2 funky-shape-depth) 0] shape))
    (fn [shape] (->> shape
                     (mirror [1 0 0])
                     (mirror [0 1 0])
                     (translate [0 (* -1/2 funky-shape-depth) 0])))
    (fn [shape] (->> shape
                     (rotate (- (/ τ 4)) [0 0 1])
                     (translate [(* 1/2 funky-shape-width) 0 0])))]
   [(translate [-20 0 0] (cube 40 80 40))
    (translate [20 -40 0] (cube 40 80 40))
    (translate [20 40 0] (cube 40 80 40))]
   gasket-shell))

(doseq [[partno part] (map vector (range) gasket-with-joints-pieces)]
  (spit (format "things/minktest-%02d.scad" partno)
        (write-scad part)))

(spit "things/minktest.scad"
      (write-scad
       (union
        (x-hollow-pins 20)
        (x-hollow-holes 20))))
