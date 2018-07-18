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
(ns dactyl-keyboard.bottom
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [dactyl-keyboard.switch-hole :refer :all]
            [dactyl-keyboard.keycaps :refer :all]
            [dactyl-keyboard.placement :refer :all]
            [dactyl-keyboard.layout :refer :all]
            [dactyl-keyboard.connectors :refer :all]
            [dactyl-keyboard.screw-hole :refer :all]
            [dactyl-keyboard.sides :refer :all]
            [unicode-math.core :refer :all]))

(def bottom-distance (- (+ sides-downness (* sides-radius 3/10))))
(def screw-hole-pillar-height (+ (Math/abs (float bottom-distance))
                                 (- sides-radius sides-thickness)))

(def bottom
  (let [finger-big-intersection-shape
        (finger-prism (* distance-below-to-intersect 5) 0)
        thumb-big-intersection-shape
        (thumb-prism (* thumb-distance-below 5) -5)
        sph1 (with-fn gasket-sphere-fn (sphere sides-radius))
        sph0 (with-fn gasket-sphere-fn (sphere (- sides-radius
                                                  sides-thickness)))
        floor-scale 0.9
        pre-scale 0.7
        for-finger
        (fn [sh] (for [row rows #_(range (- (first rows) 1)
                                         (+ (last rows) 1))]
                   (for [col columns #_(range (- (first columns) 1)
                                              (+ (last columns) 1))]
                     (if (finger-has-key-place-p row col)
                       (->> sh
                            (translate [0 0 bottom-distance])
                            (key-place col row))))))
        legs-for-finger
        (fn [sh] (for [row rows #_(range (- (first rows) 1)
                                         (+ (last rows) 1))]
                   (for [col columns #_(range (- (first columns) 1)
                                              (+ (last columns) 1))]
                     (if (finger-has-key-place-p row col)
                       (hull
                        (->> sh
                             (scale [pre-scale pre-scale 1])
                             (translate [0 0 bottom-distance])
                             (key-place col row))
                        (if (some #(= [:k col row] %) legs-at)
                          (->> sh
                               (scale [pre-scale pre-scale 1])
                               (translate [0 0 bottom-distance])
                               (key-place col row)
                               (downward-shadow 1)
                               (scale [floor-scale floor-scale 1]))))))))
        for-thumb
        (fn [sh] (for [row [-1 0 1] #_[-2 -1 0 1 2]]
                   (for [col [0 1 2] #_[-1 0 1 2 3]]
                     (->> sh
                          (translate [0 0 bottom-distance])
                          (thumb-place col row)))))
        legs-for-thumb
        (fn [sh] (for [row [-1 0 1] #_[-2 -1 0 1 2]]
                   (for [col [0 1 2] #_[-1 0 1 2 3]]
                     (hull
                      (->> sh
                           (scale [pre-scale pre-scale 1])
                           (translate [0 0 bottom-distance])
                           (thumb-place col row))
                      (if (some #(= [:t col row] %) legs-at)
                        (->> sh
                             (scale [pre-scale pre-scale 1])
                             (translate [0 0 bottom-distance])
                             (thumb-place col row)
                             (downward-shadow 1)
                             (scale [floor-scale floor-scale 1])))))))
        ; this -5 sets how far away from the keys the top of the
                                        ; marshmallowy sides will be.
                                        ; 6 was orig written in silo
                                        ; definition. from there i
                                        ; don't kknow where it came
                                        ; from.
        kp-down (+ 6 sides-radius)
        kp-shrink -20 ; percent
        extra-finger-silos
        (fn [& coordss]
          (apply hull
                 (for [[column row] coordss]
                   (silo kp-down distance-below-to-intersect kp-shrink
                       key-place key-silo-widenings column row (sa-cap 1)))))
        key-prism (union
                   (thumb-key-prism kp-down thumb-distance-below kp-shrink)
                   (finger-key-prism kp-down distance-below-to-intersect kp-shrink)
                                        ; FUDGE: remove extra bits of top of finger hull grid
                   (extra-finger-silos [2 4] [2 5])
                   (extra-finger-silos [3 4] [3 5]))
        big-marshmallow (union
                         (hull-a-grid (for-finger sph1))
                         (hull-a-grid (for-thumb sph1)))
        raw-pillars
        (apply union
               (for [hole screw-holes-at]
                 (->> (screw-hole-pillar-lower screw-hole-pillar-height)
                      (translate [0 0 (- (- plate-thickness web-thickness))])
                      ((key-place-fn hole)))))
        pillars (intersection raw-pillars big-marshmallow)
        ]
    (union
    (intersection
     (difference
      (union
       big-marshmallow
       (apply union (legs-for-finger sph1))
       (apply union (legs-for-thumb sph1)))
      (union
       (hull-a-grid (for-finger sph0))
       (hull-a-grid (for-thumb sph0)))
      key-prism)
     (union
      finger-big-intersection-shape
      thumb-big-intersection-shape))
    pillars)))

(def bottom-right
  (render bottom))
