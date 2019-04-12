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
(ns dactyl-keyboard.frame-glue-joint
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.switch-hole :refer :all]))

; Paddles that stick out below, so the fingers and thumb can be
; printed separately then glued together.

(def glue-joint-height (* 2 plate-thickness))
(def glue-joint-wall-thickness 1.2) ; how much plastic for the paddle
(def glue-joint-glue-thickness 0.1) ; how much thickness to leave for glue

(def glue-joint-lug
  (cube (/ glue-joint-wall-thickness 2)
        (/ mount-height 4)
        (/ glue-joint-height 4)))

(def glue-joint-not-lug
  (let [tolerance (* 2 glue-joint-glue-thickness)]
    (cube (+ (/ glue-joint-wall-thickness 2) tolerance)
          (+ (/ mount-height 4) tolerance)
          (+ (/ glue-joint-height 4) tolerance))))

(defn glue-joint-l-places [shape]
  (union
   (translate [(- 0 (/ glue-joint-wall-thickness 2))
               (- 0 (/ mount-height 4))
               (- 0 (/ glue-joint-height 4))]
              shape)
   (translate [(- 0 (/ glue-joint-wall-thickness 2))
               (/ mount-height 4)
               (/ glue-joint-height 4)]
              shape)))

(defn glue-joint-r-places [shape]
  (union
   (translate [(/ glue-joint-wall-thickness 2)
               (- 0 (/ mount-height 4))
               (- 0 (/ glue-joint-height 4))]
              shape)
   (translate [(/ glue-joint-wall-thickness 2)
               (/ mount-height 4)
               (/ glue-joint-height 4)]
              shape)))

                                        ; glue-joint-r-shape is at the
                                        ; far left of a key space, and
                                        ; goes on the right side of a
                                        ; glue joint
(def glue-joint-r-shape
  (difference
         (cube glue-joint-wall-thickness
               mount-height
               glue-joint-height)
         (glue-joint-r-places glue-joint-not-lug)))

                                        ; glue-joint-l-shape is at the
                                        ; far right of a key space,
                                        ; and goes on the left side of
                                        ; a glue joint
(def glue-joint-l-shape
  (union
         (cube glue-joint-wall-thickness
               mount-height
               glue-joint-height)
         (glue-joint-l-places glue-joint-lug)))

(def glue-joint-tb-shape (cube mount-width
                                glue-joint-wall-thickness
                                glue-joint-height))

  (def glue-joint-center-left
    (->> glue-joint-l-shape
         (translate [(/ glue-joint-glue-thickness 2)
                     0
                     (- plate-thickness (/ glue-joint-height 2))])))
  (def glue-joint-center-right
    (->> glue-joint-r-shape
         (translate [(- 0 glue-joint-wall-thickness (/ glue-joint-glue-thickness 2))
                     0
                     (- plate-thickness (/ glue-joint-height 2))])))

