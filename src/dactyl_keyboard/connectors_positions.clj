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
(ns dactyl-keyboard.connectors-positions
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [dactyl-keyboard.switch-hole :refer :all]
            [dactyl-keyboard.placement :refer :all]
            [dactyl-keyboard.layout :refer :all]
            [dactyl-keyboard.screw-hole :refer :all]
            ))

(def post-size 0.1)
(def post-adj (/ post-size 2))

(def se #(translate [(- (/ mount-width 2) post-adj)
                     (+ (/ mount-height -2) post-adj) 0] %))
(def sw #(translate [(+ (/ mount-width -2) post-adj)
                      (+ (/ mount-height -2) post-adj) 0] %))
(def ne #(translate [(- (/ mount-width 2) post-adj)
                      (- (/ mount-height 2) post-adj) 0] %))
(def nw #(translate [(+ (/ mount-width -2) post-adj)
                      (- (/ mount-height 2) post-adj) 0] %))
(def n #(translate [0 (- (/ mount-height 2) post-adj) 0] %))
(def s #(translate [0 (+ (/ mount-height -2) post-adj) 0] %))
(def w #(translate [(+ (/ mount-width -2) post-adj) 0 0] %))
(def e #(translate [(- (/ mount-width 2) post-adj) 0 0] %))

