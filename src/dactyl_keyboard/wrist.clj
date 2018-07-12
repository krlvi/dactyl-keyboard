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
(ns dactyl-keyboard.wrist
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.util :refer :all]))

(def pad-corner-r 11)
(def pad-width 95)
(def pad-length 76.5)

(def stand-height 45)
(def stand-thickness 1.5)

(def bumper-diameter 9.6)
(def bumper-radius (/ bumper-diameter 2))

(def corner-circle (->> (cylinder pad-corner-r stand-thickness)
                        (translate [(- (/ pad-width 2) pad-corner-r)
                                    (- (/ pad-length 2) pad-corner-r)
                                    (+ stand-height (/ stand-thickness 2))])))

(def corner {:back-right corner-circle
             :back-left (->> corner-circle (mirror [-1 0 0]))
             :front-right (->> corner-circle (mirror [0 -1 0]))
             :front-left (->> corner-circle (mirror [0 -1 0]) (mirror [-1 0 0]))             })

(def wrist-rest
  (union (hull (corner :back-right)
               (corner :back-left)
               (corner :front-right)
               (corner :front-left))
         (bottom-hull (corner :back-right))
         (bottom-hull (corner :back-left))
         (bottom-hull (corner :front-right))
         (bottom-hull (corner :front-left))))

(spit "things/wrist.scad"
      (write-scad wrist-rest))
