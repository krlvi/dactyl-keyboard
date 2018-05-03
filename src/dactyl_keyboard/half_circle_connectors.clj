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

(defn x-half-cylinder-common [nudge gasket-shape-radius height position]
  (let [r (match [gasket-shape-radius]
                 [[gsr1 gsr2]] [(+ gsr1 pin-tolerance) gsr2]
                 [gsr] (+ gsr pin-tolerance))
        bigger (+ 2 (* 2 (if (vector? gasket-shape-radius)
                           (first gasket-shape-radius)
                           gasket-shape-radius)))
        half (translate [0 (+ (* 1/2 bigger) nudge) 0]
                        (cube bigger bigger bigger))]
    (->> (cylinder r height)
         (rotate (/ τ 4) [0 1 0])
         (intersection half)
         (translate [(* 1/2 height) 0 0])
         (translate [position 0 0]))))

(def x-half-cylinder-for-diff (partial x-half-cylinder-common (- ε)))
(def x-half-cylinder (partial x-half-cylinder-common 0))

(defn x-pins-places [radius shape]
  (let [axify #(vector 0 %1 %2)
        spacing (* 1.0 pin-length) ; should not depend on params: we use
                                        ; different tooth sizes and
                                        ; radii but need same places
        npins (+ (/ radius spacing) 3)
        polygonal-flat-diameter-ratio (Math/sin (/ τ pin-fn))]
    (apply union
           (for [x (range -1 (- npins 1))
                 y (range (- npins) npins)]
             (->> shape
                  (rotate (* τ 1/4) [0 1 0])
                  (translate (axify
                              (* x spacing polygonal-flat-diameter-ratio)
                              (* y 3/4 spacing)))
                  (translate (axify (* (mod y 2) 1/2 pin-length) 0))
                  (translate (axify 0 (* y -1/12 spacing))))))))

(defn x-pin [length]
  (with-fn pin-fn
    (cylinder [length 0] length)))


(defn x-solid-pin-cone [gasket-shape-radius pin-r-factor]
  (let [r gasket-shape-radius
        long (* 1.5 r)
        pin-r (* pin-r-factor r)
        pin-crop (x-half-cylinder [pin-r 0] pin-r 0)
        pins-front (intersection
                    (x-pins-places gasket-shape-radius (x-pin pin-length))
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
  (x-half-cylinder (* 2 gasket-shape-radius)
                   (* 2 pin-tolerance)
                   (- 0 pin-tolerance ε)))

(defn x-connect-common [x-offset gasket-shape-radius place shape]
  (let [section (x-half-cylinder gasket-shape-radius
                                 interface-thickness
                                 x-offset)
        core (x-half-cylinder-for-diff
              (* 0.99 gasket-shape-radius)
              (* 2 interface-thickness)
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

