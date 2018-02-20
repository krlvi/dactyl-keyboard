(ns dactyl-keyboard.minktest
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [unicode-math.core :refer :all]
            [clojure.core.match :refer [match]]))

; https://tauday.com
(def τ (* π 2))

; csg food
(def ε 0.001)

(def √2 (Math/sqrt 2))

(def funky-shape-width 20)
(def funky-shape-depth 15)
(def funky-shape-height 30) ; must > twice outer-gasket-radius
(defn funky-shape [shrink]
  (let [shrink-factor (/ (- 100 shrink) 100)
        fsw (* funky-shape-width shrink-factor)
        fsd (* funky-shape-depth shrink-factor)
        fsh (* funky-shape-height shrink-factor)]
  (cube fsw fsd fsh)))

(def ribbon (intersection
             (difference (funky-shape 0) (funky-shape 0.01))
             (cube 400 400 0.2)))

(def outer-gasket-radius 15)
(def gasket-shell-radius (- outer-gasket-radius 2))

(defn gasket-shape [radius]
  (let [diameter (* 2 radius)]
    (binding [*fn* 16] (sphere radius))))

(def gasket (minkowski ribbon (gasket-shape outer-gasket-radius)))

(def gasket-shell
  (let [little-gasket (minkowski ribbon (gasket-shape gasket-shell-radius))]
    (difference
     (difference gasket little-gasket)
     (funky-shape 0))))

(def pin-tolerance 0.01)
(def pin-length 3)
(def pin-side-length (/ (* 2 pin-length) √2))
(def interface-thickness 1)

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
  (let [spacing (* 1.3 pin-length) ; should not depend on params: we use
                                        ; different tooth sizes and
                                        ; radii but need same places
        npins (+ (/ radius spacing) 3)]
    (apply union
           (for [x 
                 (range -1 (- npins 1)) y
                  (range (- npins) npins)]
             (->> shape
                  (rotate (* τ 3/8) [1 0 0])
                  (translate [0 (* x spacing) (* y spacing)])
                  (translate [0 (if (= 0 (mod y 2)) (* 1/2 spacing) 0) 0]))))))

(defn x-pin [length]
  (let [side (/ (* 2 length) √2)] 
    (->> (cube side side side)
         (rotate (* τ 1/8) [0 1 0])
         (rotate (* τ 1/8) [1 0 0])
         (rotate (* τ 1/8) [1 0 0])
         (translate [0 0 0]))))

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

(def gasket-with-joints-pieces
  (pieces-with-x-pins-and-holes
   gasket-shell-radius
   [(fn [shape] (translate [0 (* 1/2 funky-shape-depth) 0] shape))
    (fn [shape] (->> shape
                     (mirror [1 0 0])
                     (mirror [0 1 0])
                     (translate [0 (* -1/2 funky-shape-depth) 0])))
    (fn [shape] (->> shape
                     (rotate (- (/ τ 4)) [0 0 1])
                     (translate [(* 1/2 funky-shape-width) 0 0])))]
   [(translate [-20 0 0] (cube 40 80 40))
    (translate [20 -40 0] (cube 40 80 40))
    (translate [20 40 0] (cube 40 80 40))]
   gasket-shell))

(doseq [[partno part] (map vector (range) gasket-with-joints-pieces)]
  (spit (format "things/minktest-%02d.scad" partno)
        (write-scad part)))

(spit "things/minktest.scad"
      (write-scad
       (union
        (x-pins 20)
        (x-holes 20))))
