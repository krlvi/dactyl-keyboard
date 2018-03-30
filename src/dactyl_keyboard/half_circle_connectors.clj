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

(defn half-cylinder-common-common [cube-translate-vector nudge gasket-shape-radius height position]
  (let [r (match [gasket-shape-radius]
                 [[gsr1 gsr2]] [(+ gsr1 pin-tolerance) gsr2]
                 [gsr] (+ gsr pin-tolerance))
        bigger (+ 2 (* 2 (if (vector? gasket-shape-radius)
                           (first gasket-shape-radius)
                           gasket-shape-radius)))
        cube-translate (map #(* % (+ (* 1/2 bigger) nudge)) cube-translate-vector)
        half (translate (apply vector cube-translate)
                        (cube bigger bigger bigger))]
    (->> (cylinder r height)
         (rotate (/ τ 4) [0 1 0])
         (intersection half)
         (translate [(* 1/2 height) 0 0])
         (translate [position 0 0]))))

(def xu-half-cylinder-common
  (partial half-cylinder-common-common [0 0 -1]))
(def x-half-cylinder-common
  (partial half-cylinder-common-common [0 1 0]))

(def xu-half-cylinder-for-diff (partial xu-half-cylinder-common (- ε)))
(def xu-half-cylinder (partial xu-half-cylinder-common 0))

(def x-half-cylinder-for-diff (partial x-half-cylinder-common (- ε)))
(def x-half-cylinder (partial x-half-cylinder-common 0))

(defn pins-places-common [axify radius shape]
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
                  (translate (axify
                              (* x spacing polygonal-flat-diameter-ratio)
                              (* y 3/4 spacing)))
                  (translate (axify (* (mod y 2) 1/2 pin-length) 0))
                  (translate (axify 0 (* y -1/12 spacing))))))))
(def x-pins-places (partial pins-places-common #(vector 0 %1 %2)))
(def xu-pins-places (partial pins-places-common #(vector 0 %2 (- %1))))


(defn x-pin [length]
  (with-fn pin-fn
    (cylinder [length 0] length)))


(defn solid-pins-common [pins-places
                          half-cylinder
                          gasket-shape-radius
                          pin-r-factor]
  (let [r gasket-shape-radius
        long (* 1.5 r)
        pin-r (* pin-r-factor r)
        pin-crop (half-cylinder [pin-r 0] pin-r 0)
        pins-front (intersection
                    (pins-places gasket-shape-radius (x-pin pin-length))
                    pin-crop)]
    pins-front))
(def xu-solid-pins (partial solid-pins-common xu-pins-places
                            xu-half-cylinder))

(defn hollow-pins-common [pins-places
                          half-cylinder
                          half-cylinder-for-diff
                          inside-slice
                          gasket-shape-radius
                          pin-r-factor back-r-factor
                          pin-length back-length
                          shape]
  (let [r gasket-shape-radius
        long (* 1.5 r)
        pin-r (* pin-r-factor r)
        back-r (* back-r-factor r)
        pin-crop (half-cylinder [pin-r 0] pin-r 0)
        back-crop (half-cylinder [back-r 0] (+ back-r (* 2 ε)) (- ε))
        back-remove (half-cylinder-for-diff back-r (+ long ε) (* -1/2 long))
        pins-front (intersection
                    (pins-places gasket-shape-radius (x-pin pin-length))
                    pin-crop)
        pins-back (intersection
                   (pins-places gasket-shape-radius (x-pin back-length))
                   back-crop)]
    (union
     (difference shape back-remove)
     (difference pins-front pins-back (inside-slice pin-length gasket-shape-radius)))))
(defn x-inside-slice [pin-length gasket-shape-radius]
  (cube (* 2 pin-length) ε (* 2 gasket-shape-radius)))
(defn xu-inside-slice [pin-length gasket-shape-radius]
  (cube (* 2 pin-length) (* 2 gasket-shape-radius) ε))
(def x-hollow-pins (partial hollow-pins-common
                            x-pins-places x-half-cylinder
                            x-half-cylinder-for-diff
                            x-inside-slice))
(def xu-hollow-pins (partial hollow-pins-common
                             xu-pins-places xu-half-cylinder
                             xu-half-cylinder-for-diff
                             xu-inside-slice))

(defn pin-block-common-common [thickness half-cylinder gasket-shape-radius]
  (half-cylinder gasket-shape-radius interface-thickness (- interface-thickness)))
(def pin-block-common (partial pin-block-common-common interface-thickness))
(def pin-block-for-hull-common (partial pin-block-common-common ε))

(def x-pin-block (partial pin-block-common x-half-cylinder))
(def xu-pin-block (partial pin-block-common xu-half-cylinder))
(def x-pin-block-for-hull (partial pin-block-for-hull-common x-half-cylinder))
(def xu-pin-block-for-hull (partial pin-block-for-hull-common xu-half-cylinder))

(defn pins-common [pin-block hollow-pins gasket-shape-radius]
  (let [pin-block (pin-block gasket-shape-radius)]
    (hollow-pins gasket-shape-radius 0.85 0.8
                 pin-length
                 (- pin-length (* √2 interface-thickness))
                 pin-block)))
(def x-pins (partial pins-common x-pin-block x-hollow-pins))
(def xu-pins (partial pins-common xu-pin-block xu-hollow-pins))

(defn holes-common [half-cylinder hollow-pins gasket-shape-radius]
  (let [hole-block-height interface-thickness
        hole-block (half-cylinder gasket-shape-radius hole-block-height 0)]
    (translate [pin-tolerance 0 0]
               (hollow-pins gasket-shape-radius 0.9 0.85
                            (+ pin-length (* √2 interface-thickness))
                            pin-length
                            hole-block))))
(def x-holes (partial holes-common x-half-cylinder x-hollow-pins))
(def xu-holes (partial holes-common xu-half-cylinder xu-hollow-pins))

(defn gap-common [half-cylinder gasket-shape-radius]
  (half-cylinder (* 2 gasket-shape-radius)
                   pin-tolerance
                   (- ε)))
(def x-gap (partial gap-common x-half-cylinder))
(def xu-gap (partial gap-common xu-half-cylinder))

(defn connect-common-common [x-offset
                             half-cylinder
                             half-cylinder-for-diff
                             gasket-shape-radius place shape]
  (let [
        section (half-cylinder gasket-shape-radius
                                 interface-thickness
                                 x-offset)
        core (half-cylinder-for-diff
              (* 0.99 gasket-shape-radius)
              (* 2 interface-thickness)
              (+ (* -1/2 interface-thickness)
                 x-offset))
        intersect (half-cylinder
                   (* 2 gasket-shape-radius)
                   interface-thickness
                   x-offset)]
    (difference
     (hull (place section)
           (intersection shape (place intersect)))
     (place core))))
(def connect-to-hole-common (partial connect-common-common pin-tolerance))
(def connect-to-pin-common (partial connect-common-common (- interface-thickness)))
(def x-connect-to-hole (partial connect-to-hole-common
                                x-half-cylinder x-half-cylinder-for-diff))
(def xu-connect-to-hole (partial connect-to-hole-common
                                 xu-half-cylinder xu-half-cylinder-for-diff))
(def x-connect-to-pin (partial connect-to-pin-common
                               x-half-cylinder x-half-cylinder-for-diff))
(def xu-connect-to-pin (partial connect-to-pin-common
                                xu-half-cylinder xu-half-cylinder-for-diff))


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
             (jp1 (x-pins r))
             (x-connect-to-hole x-pins-radius jp2 thing-to-be-split)
             (jp2 (x-holes r))))))

