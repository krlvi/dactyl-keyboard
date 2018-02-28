(ns dactyl-keyboard.half-circle-connectors
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [unicode-math.core :refer :all]
            [clojure.core.match :refer [match]]))

(def ^:const pin-tolerance 0.2)
(def ^:const pin-length 7)
(def ^:const pin-fn 6) ; hexagonal cones
(def ^:const interface-thickness 1.3)

(defn x-half-cylinder-common [gasket-shape-radius height position nudge]
  (let [r (match [gasket-shape-radius]
                 [[gsr1 gsr2]] [(+ gsr1 pin-tolerance) gsr2]
                 [gsr] (+ gsr pin-tolerance))
        bigger (+ 2 (* 2 (if (vector? gasket-shape-radius)
                           (first gasket-shape-radius)
                           gasket-shape-radius)))
        half (translate [0 (+ (* 1/2 bigger) nudge) 0]
                        (cube bigger bigger bigger))]
    (->> (intersection half (cylinder r height))
         (rotate (/ τ 4) [0 1 0])
         (translate [(* 1/2 height) 0 0])
         (translate [position 0 0]))))
(defn x-half-cylinder-for-diff [gasket-shape-radius height position]
  (x-half-cylinder-common gasket-shape-radius height position (- ε)))
(defn x-half-cylinder [gasket-shape-radius height position]
  (x-half-cylinder-common gasket-shape-radius height position 0))

(defn x-pins-places [radius shape]
  (let [spacing (* 1.0 pin-length) ; should not depend on params: we use
                                        ; different tooth sizes and
                                        ; radii but need same places
        npins (+ (/ radius spacing) 3)
        polygonal-flat-diameter-ratio (Math/sin (/ τ pin-fn))]
    (apply union
           (for [x (range -1 (- npins 1))
                 y (range (- npins) npins)]
             (->> shape
                  (rotate (* τ 1/4) [0 1 0])
                  (translate [0
                              (* x spacing polygonal-flat-diameter-ratio)
                              (* y 3/4 spacing)])
                  (translate [0 (* (mod y 2) 1/2 pin-length) 0])
                  (translate [0 0 (* y -1/12 spacing)]))))))

(defn x-pin [length]
  (with-fn pin-fn
    (cylinder [length 0] length)))

(defn x-hollow-pins [gasket-shape-radius
                     pin-r-factor back-r-factor
                     pin-length back-length
                     shape]
  (let [r gasket-shape-radius
        long (* 1.5 r)
        pin-r (* pin-r-factor r)
        back-r (* back-r-factor r)
        pin-crop (x-half-cylinder [pin-r 0] pin-r 0)
        back-crop (x-half-cylinder [back-r 0] (+ back-r (* 2 ε)) (- ε))
        back-remove (x-half-cylinder-for-diff back-r (+ long ε) (* -1/2 long))
        pins-front (intersection
                    (x-pins-places gasket-shape-radius (x-pin pin-length))
                    pin-crop)
        pins-back (intersection
                   (x-pins-places gasket-shape-radius (x-pin back-length))
                   back-crop)
        inside-slice (cube (* 2 pin-length) ε (* 2 gasket-shape-radius))]
    (union
     (difference shape back-remove)
     (difference pins-front pins-back inside-slice))))

(defn x-pins [gasket-shape-radius]
  (let [pin-block-height interface-thickness
        pin-block (x-half-cylinder gasket-shape-radius pin-block-height
                                   (- pin-block-height))]
    (x-hollow-pins gasket-shape-radius 0.85 0.8
                   pin-length
                   (- pin-length interface-thickness)
                   pin-block)))

(defn x-holes [gasket-shape-radius]
  (let [hole-block-height interface-thickness
        hole-block (x-half-cylinder gasket-shape-radius hole-block-height 0)]
    (translate [pin-tolerance 0 0]
               (x-hollow-pins gasket-shape-radius 0.9 0.85
                              (+ pin-length interface-thickness)
                              pin-length
                              hole-block))))
                   
(defn connect-common [gasket-shape-radius place shape x-offset]
  (let [
        section (x-half-cylinder gasket-shape-radius
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
(defn connect-to-hole [gasket-shape-radius place shape]
  (connect-common gasket-shape-radius place shape pin-tolerance))
(defn connect-to-pin [gasket-shape-radius place shape]
  (connect-common gasket-shape-radius place shape (- interface-thickness)))

(defn x-gap [gasket-shape-radius]
  (x-half-cylinder (* 2 gasket-shape-radius)
                   pin-tolerance
                   (- ε)))

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
             (connect-to-pin x-pins-radius jp1 thing-to-be-split)
             (jp1 (x-pins r))
             (connect-to-hole x-pins-radius jp2 thing-to-be-split)
             (jp2 (x-holes r))))))

