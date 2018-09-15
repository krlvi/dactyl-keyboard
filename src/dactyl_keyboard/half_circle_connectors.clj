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
(ns dactyl-keyboard.half-circle-connectors
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [unicode-math.core :refer :all]
            [clojure.core.match :refer [match]]))

(def ^:const pin-tolerance 0.3)
(def ^:const pin-length 9)
(def ^:const pin-fn 6) ; hexagonal cones
(def ^:const interface-thickness 2)

;; This is necessary because the amount by which the key frusta and
;; key prisms cut the marshmallow gasket leaves it slightly less than
;; semicircular in some places; if the connector protrudes beyond the
;; residual half-gasket, the bottom won't fit. (issue #41)
(def ^:const amount-of-half-circle 0.85)

(defn x-half-cylinder-common [amount nudge gasket-shape-radius height position]
  (let [r (match [gasket-shape-radius]
                 [[gsr1 gsr2]] [(+ gsr1 pin-tolerance) gsr2]
                 [gsr] (+ gsr pin-tolerance))
        bigger (+ 2 (* 2 (if (vector? gasket-shape-radius)
                           (first gasket-shape-radius)
                           gasket-shape-radius)))
        one-r (match [gasket-shape-radius]
                     [[gsr1 gsr2]] (max gsr1 gsr2)
                     [gsr] gsr)
        nq-half-circle-nudge (* one-r (- 1.0 amount))
        half (translate [0 (+ (* 1/2 bigger) nudge nq-half-circle-nudge) 0]
                        (cube bigger bigger bigger))]
    (->> (cylinder r height)
         (rotate (/ τ 4) [0 1 0])
         (intersection half)
         (translate [(* 1/2 height) 0 0])
         (translate [position 0 0]))))

(def x-half-cylinder-for-diff (partial x-half-cylinder-common 1.0 (- ε)))
(def x-half-cylinder-for-intersect (partial x-half-cylinder-common 1.0 0))
(def x-half-cylinder (partial x-half-cylinder-common amount-of-half-circle 0))

(defn x-bumpy-thing [r length]
  (let [sample-factor 1/15
        sample-size [(* sample-factor r)
                     (* sample-factor r)
                     (* sample-factor r)]
        fy (/ 3 (max r 15))
        fz (/ 2 (max r 15))
        frequency [1 fy fz]
        amplitude (* 1/4 length)]
    (->>
     (call-module "x_single_eggcrate_box"
                  [(* 1/2 length) r (* 2 r)]
                  sample-size
                  frequency
                  amplitude)
     (translate [(+ #_interface-thickness
                    (* 1/2 amplitude))
                 0 (- r)]))))

(defn x-solid-pin-cone [gasket-shape-radius pin-r-factor]
  (let [r gasket-shape-radius
        long (* 1.5 r)
        pin-r (* pin-r-factor r)
        pin-crop (x-half-cylinder [pin-r 0] pin-r 0)
        pins-front (intersection
                    (x-bumpy-thing r pin-r)
                    pin-crop)]
    pins-front))

(defn x-pin-block-common [thickness gasket-shape-radius]
  (x-half-cylinder gasket-shape-radius interface-thickness (- interface-thickness)))
(def x-pin-block (partial x-pin-block-common interface-thickness))
(def x-pin-block-for-hull (partial x-pin-block-common ε))

(defn x-solid-pins [gasket-shape-radius]
  (union (x-pin-block gasket-shape-radius)
         (x-solid-pin-cone gasket-shape-radius 0.85)))

(defn x-hollow-pins [gasket-shape-radius]
  (difference (x-solid-pins gasket-shape-radius)
              (translate [(- 0 ε (* √2 interface-thickness)) (- ε) 0]
                         (x-solid-pin-cone gasket-shape-radius 0.85))))

(defn x-solid-holes [gasket-shape-radius]
  (let [hole-block-height (- pin-length interface-thickness)
        hole-block (x-half-cylinder gasket-shape-radius hole-block-height 0)]
    (translate [pin-tolerance 0 0]
               (difference hole-block
                           (translate [(- ε) (- ε) 0]
                                      (x-solid-pin-cone gasket-shape-radius 0.85))))))
(defn x-solid-holes-for-hull [gasket-shape-radius] (x-half-cylinder gasket-shape-radius ε (- pin-length interface-thickness)))

(defn x-hollow-holes [gasket-shape-radius]
  (let [hole-block (x-half-cylinder gasket-shape-radius
                                    (* √2 interface-thickness) 0)]
    (translate [pin-tolerance 0 0]
               (difference
                (union hole-block
                       (translate [(* √2 interface-thickness) 0 0]
                                  (x-solid-pin-cone gasket-shape-radius 0.85)))
                (translate [(- ε) (- ε) 0]
                           (x-solid-pin-cone gasket-shape-radius 0.85))))))

(defn x-gap [gasket-shape-radius]
  (x-half-cylinder-for-intersect
   (* 2 gasket-shape-radius)
   (* 2 pin-tolerance)
   (- 0 pin-tolerance ε)))

(defn x-connect-common [x-offset gasket-shape-radius place shape]
  (let [section (x-half-cylinder gasket-shape-radius
                                 interface-thickness
                                 x-offset)
        core (x-half-cylinder-for-diff
              (* 1 gasket-shape-radius)
              (+ (* 2 interface-thickness) pin-length)
              (+ (* -1/2 interface-thickness)
                 x-offset))
        intersect (x-half-cylinder
                   (* 2 gasket-shape-radius)
                   interface-thickness
                   x-offset)]
    (difference
     (hull (place section)
           (intersection shape (place intersect)))
     (place core))))
(def x-connect-to-hole (partial x-connect-common pin-tolerance))
(def x-connect-to-pin (partial x-connect-common (- interface-thickness)))

(defn pieces-with-x-pins-and-holes [x-pins-radius
                                    joint-places
                                    intersection-shapes
                                    thing-to-be-split]
  (let [r x-pins-radius
        joint-places-rot1 (concat (rest joint-places)
                                  [(first joint-places)])]
    (for [[jp1 jp2 is] (map vector joint-places
                            joint-places-rot1
                            intersection-shapes)]
      (union (intersection
              (difference thing-to-be-split (jp2 (x-gap r)))
              is)
             (x-connect-to-pin x-pins-radius jp1 thing-to-be-split)
             (jp1 (x-hollow-pins r))
             (x-connect-to-hole x-pins-radius jp2 thing-to-be-split)
             (jp2 (x-hollow-holes r))))))

(defn pieces-with-x-pins-and-holes-faster [x-pins-radius
                                           joint-places
                                           intersection-shapes
                                           thing-to-be-split
                                           approximations]
  "This attaches the pins and holes to a succession of objects that
  locally approximate a larger or more complicated object, to avoid
  using the larger object as many times and slowing down OpenSCAD."
  (let [r x-pins-radius
        joint-places-rot1 (concat (rest joint-places)
                                  [(first joint-places)])
        approximations-rot1 (concat (drop 1 approximations)
                                    (take 1 approximations))]
    (for [[jp1 jp2 is appx1 appx2] (map vector joint-places
                                 joint-places-rot1
                                 intersection-shapes
                                 approximations
                                 approximations-rot1)]
      (union (intersection
              (difference thing-to-be-split (jp2 (x-gap r)))
              is)
             (x-connect-to-pin x-pins-radius jp1 appx1)
             (jp1 (x-hollow-pins r))
             (x-connect-to-hole x-pins-radius jp2 appx2)
             (jp2 (x-hollow-holes r))))))

