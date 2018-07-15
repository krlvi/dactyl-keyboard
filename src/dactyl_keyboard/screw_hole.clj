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
(ns dactyl-keyboard.screw-hole
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [dactyl-keyboard.switch-hole :refer [web-thickness]]
            [unicode-math.core :refer :all]))

;; Heat-Set Inserts for Plastics
;; https://www.mcmaster.com/#94180A331
;; https://www.matterhackers.com/articles/fasteners-for-3d-printing

(def insert-hole-bottom-diameter 5.1)
(def insert-hole-top-diameter 5.31)
(def insert-height 3.8)
;; #31
(def insert-hole-depth (+ insert-height 2.0))

;; thicker, and you impinge on the switch holes... with the α and β I
;; used anyway.  but too thin and you start squishing the walls when
;; you melt the insert in
(def screw-hole-wall-thickness 1.8)

;; M3 screws.
(def screw-diameter 3)
(def screw-radius (* 1/2 screw-diameter))

;; this would have been (+ plate-thickness insert-height) but
;; they don't sell custom screws, so i did the math and figured a
;; standard size.
;;
;; https://www.mcmaster.com/#90116A153
(def frame-screw-length 8)

(def frame-screw-hole
  (with-fn 12
    (cylinder screw-radius (* 3 web-thickness))))

;; leave room for diodes and wires; too tall and the teensy runs into
;; the bottom
(def teensy-screw-hole-height (+ insert-hole-depth 5))

;; top is at z=0
(def insert-boss
  (let [bottom-radius (* 1/2 insert-hole-bottom-diameter)
        top-radius (* 1/2 insert-hole-top-diameter)]
    (translate [0 0 (* -1/2 insert-hole-depth)]
               (cylinder [bottom-radius top-radius] insert-hole-depth))))

(def screw-hole-base-diameter 25)
(defn screw-hole-pillar [height]
  (let [thic screw-hole-wall-thickness
        top-r (+ (* 1/2 insert-hole-top-diameter) thic)
        bottom-r (* 1/2 screw-hole-base-diameter)
        power 6 ; determines how sharply the horn curves. should be even.
        a (/ (- bottom-r top-r) (Math/pow (- thic height) power))
        f (fn [y] (+ top-r (* a (Math/pow (- y height) power))))
        steps 5
        step-size (/ height steps)
        horn
        (apply union
               (for [y (range 0 height step-size)]
                 (let [ny (+ y step-size)
                       y-r (f y)
                       ny-r (f ny)]
                   (->> (with-fn 12 (cylinder [y-r ny-r] step-size))
                        (translate [0 0 (- (+ y (* 1/2 step-size))
                                           height)])))))]
    (difference horn
                (->> insert-boss
                     (color [1 0 0])
                     (translate [0 0 ε])))))

(def screw-hole-pillar-glue-tolerance 0.2)

(defn screw-hole-pillar-splitter [height]
  (let [height-fraction 1/16
        tz (* height-fraction height)
        ty (* 3/2 screw-hole-base-diameter)
        tx (* 3/2 screw-hole-base-diameter)
        pattern-size 16
        all-slices
        (apply union
               (for [ab [0 1]]
                 (apply union
                        (for [slice (range (+ 2 (quot tz pattern-size)))]
                          (call-module "vertical_prisms_slice"
                                       tz ty tx pattern-size (- ε)
                                       ab slice)))))
        
        ]
    (->> all-slices
         (rotate (* 1/4 τ) [0 1 0])
         (translate [0 0 (* 1/2 tz)]))))

(defn screw-hole-pillar-upper [height]
  (difference (screw-hole-pillar height)
              (translate [0 0 (+ (- height)
                                 screw-hole-pillar-glue-tolerance)]
                         (screw-hole-pillar-splitter height))))

(defn screw-hole-pillar-lower [height]
  (intersection (screw-hole-pillar height)
                (translate [0 0 (- height)]
                           (screw-hole-pillar-splitter height))))

(def screw-hole-for-teensy
  (let [height teensy-screw-hole-height]
    (difference
     (translate [0 0 (* -1/2 height)]
                (cylinder (+ (* 1/2 insert-hole-top-diameter) screw-hole-wall-thickness)
                          height))
     (->> insert-boss
          (rotate (* 1/2 τ) [1 0 0])
          (translate [0 0 (+ (- height) (- ε))])))))
