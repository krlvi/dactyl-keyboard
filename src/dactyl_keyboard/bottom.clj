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
                                 (- sides-radius sides-thickness)
                                 ;; they are cut off at the bottom
                                 ;; so add slop
                                 3))
(def legs-glue-tolerance 0.2)

(defn leg-pillar-splitter [a-p]
  (let [height 80
        interface-height 24
        face-size 1.5
        amplitude 5
        frequency [1 1/10 1/12]
        leg-radius-fudge (* 4 sides-radius)
        max-leg-radius (* 10 sides-radius) ;; includes allowance for
        ;; being rotated athwart the leg
        pattern-size 48
        modname (if a-p
                  "x_single_eggcrate_box"
                  "x_mating_single_eggcrate_box")
        cone-radii (if a-p
                     [max-leg-radius leg-radius-fudge]
                     [leg-radius-fudge max-leg-radius])
        cone-move-way (if a-p -1 1)
        slice (call-module modname
                           [interface-height
                            (* 3/2 leg-radius-fudge)
                            (* 3/2 leg-radius-fudge)]
                           [face-size face-size face-size]
                           frequency
                           amplitude)
        interface (->> slice
                       (color [1 0 0])
                       (translate [0 (* -3/4 leg-radius-fudge)
                                   (* -3/4 leg-radius-fudge)])
                       (rotate (* -1/4 τ) [0 1 0])
                       #_(translate [0 0 (* -1/2 interface-height)])
                       #_(translate [0 0 (* -1/4 pattern-size)]))
        cone (->> (cylinder cone-radii height))
        ]
    (union interface
           (->> cone
                (translate [0 0 (* cone-move-way
                                   (+ (* 1/2 height)
                                      (* 1/2 interface-height)))])))))

(def leg-pillar-splitter-a (leg-pillar-splitter true))
(def leg-pillar-splitter-b (leg-pillar-splitter false))

(defn legs [nubsp]
  (let [leg-nub-height 2
        sph1 (with-fn gasket-sphere-fn (sphere sides-radius))
        floor-scale 0.9 ;; values smaller than 1.0 push the bottoms of
                        ;; the legs toward the origin
        pre-scale 0.7 ;; factor of sides-radius that determines leg
                      ;; diameter
        legs-for
        (fn [place-symbol place rows columns sh]
          (for [row rows col columns
                :when (some #(= [place-symbol col row] %) legs-at)]
                   (hull
                    (->> sh
                         (scale [pre-scale pre-scale pre-scale])
                         (translate [0 0 bottom-distance])
                         (place col row))
                    (->> sh
                         (scale [pre-scale pre-scale pre-scale])
                         (translate [0 0 bottom-distance])
                         (place col row)
                         (downward-shadow 1)
                         (scale [floor-scale floor-scale 1])))))
        legs-for-finger (partial legs-for :k key-place rows columns)
        legs-for-thumb (partial legs-for :t thumb-place [-1 0 1] [0 1 2])
        splitters-for
        (fn [place-symbol place rows columns sh]
          (for [row rows col columns
                :when (some #(= [place-symbol col row] %) legs-at)]
            (->> sh
                 (translate [0 0 (- 0 screw-hole-pillar-height
                                    leg-nub-height)])
                 (place col row))))
        splitters-for-thumb (partial splitters-for :t thumb-place
                                     [-1 0 1] [0 1 2])
        splitters-for-finger (partial splitters-for :k key-place
                                      rows columns)
        legs (concat (legs-for-finger sph1) (legs-for-thumb sph1))
        splitters-a
        (concat (splitters-for-finger leg-pillar-splitter-a)
                (splitters-for-thumb leg-pillar-splitter-a))
        splitters-b
        (concat (splitters-for-finger leg-pillar-splitter-b)
                (splitters-for-thumb leg-pillar-splitter-b))
        ]
    (for [[leg a b] (map vector legs splitters-a splitters-b)]
      (intersection leg (if nubsp b a)))))


(def bottom
  (let [
        finger-big-intersection-shape
        (finger-prism (* distance-below-to-intersect 5) 0)
        thumb-big-intersection-shape
        (thumb-prism (* thumb-distance-below 5) -5)
        sph1 (with-fn gasket-sphere-fn (sphere sides-radius))
        sph0 (with-fn gasket-sphere-fn (sphere (- sides-radius
                                                  sides-thickness)))
        for-finger
        (fn [sh] (for [row rows #_(range (- (first rows) 1)
                                         (+ (last rows) 1))]
                   (for [col columns #_(range (- (first columns) 1)
                                              (+ (last columns) 1))]
                     (if (finger-has-key-place-p col row)
                       (->> sh
                            (translate [0 0 bottom-distance])
                            (key-place col row))))))
        for-thumb
        (fn [sh] (for [row [-1 0 1] #_[-2 -1 0 1 2]]
                   (for [col [0 1 2] #_[-1 0 1 2 3]]
                     (->> sh
                          (translate [0 0 bottom-distance])
                          (thumb-place col row)))))
        ;; this -5 sets how far away from the keys the top of the
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
       (apply union (legs true)))
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
