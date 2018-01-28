(ns dactyl-keyboard.minktest
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [unicode-math.core :refer :all]))

; https://tauday.com
(def τ (* π 2))

(defn funky-shape [shrink]
  (let [d (- 100 shrink)]
    (rotate (/ τ 20) [0 0 1]
            (union (cube d d d)
                   (->> (cube d d d)
                        (rotate (/ τ 4) [1 1 0])
                        (translate [35 20 0]))))))
  
(def ribbon (intersection
            (difference (funky-shape 0) (funky-shape 0.01))
            (cube 400 400 0.2)))

(defn gasket-shape [radius]
  (let [diameter (* 2 radius)]
    (->> (binding [*fn* 6] (sphere radius))
         (rotate (/ τ 8) [0 0 1])
         (rotate (/ τ 8) [1 0 0]))))

(def gasket (minkowski ribbon (gasket-shape 10)))

(def gasket-shell
  (let [little-gasket (minkowski ribbon (gasket-shape 8))]
    (difference
     (difference gasket little-gasket)
     (funky-shape 0))))

(def npins 3)
(def pin-tolerance 0.5)
(def pin-length 9)
(def pin-fn 8)

(defn x-pin-places [gasket-shape-radius shape]
  (apply union
         (for [pin (range npins)]
           (->> shape
                (rotate (/ τ 4) [0 1 0])
                (translate [0 0 (* 2/3 gasket-shape-radius)])
                (rotate (* (+ 1 pin)
                           (/ (/ τ 2) (+ 1 npins))) [-1 0 0])
                (translate [(* 1/2 pin-length) 0 0])))))


(defn x-pins [gasket-shape-radius]
  (let [pin-block-height (* 1/3 pin-length)
        pin-radius (/ gasket-shape-radius 8)
        bigger (+ 2 (* 2 gasket-shape-radius))
        half (translate [0 (* 1/2 bigger) 0] (cube bigger bigger bigger))
        pin-block (->>
                   (intersection
                    half
                    (cylinder gasket-shape-radius pin-block-height))
                   (rotate (/ τ 4) [0 1 0])
                   (translate [(* 1/2 pin-block-height) 0 0]))
        pin (binding [*fn* pin-fn]
              (cylinder pin-radius pin-length))
        pins (x-pin-places gasket-shape-radius pin)]
    (union pin-block pins)))

(defn x-holes [gasket-shape-radius]
  (let [hole-block-height (- (* 2/3 pin-length) pin-tolerance)
        pin-radius (/ gasket-shape-radius 8)
        bigger (+ 2 (* 2 gasket-shape-radius))
        half (translate [0 (* 1/2 bigger) 0] (cube bigger bigger bigger))
        hole-block (->>
                    (intersection
                     half
                     (cylinder gasket-shape-radius
                               hole-block-height))
                    (rotate (/ τ 4) [0 1 0])
                    (translate [(* 1/2 hole-block-height) 0 0])
                    (translate [(- pin-length hole-block-height) 0 0]))
                                        ; * 1.2: comfortably longer than block so difference is clean
        hole (binding [*fn* pin-fn]
               (cylinder (+ pin-radius pin-tolerance)
                         (* pin-length 1.2)))
        holes (x-pin-places gasket-shape-radius hole)]
    (difference hole-block holes)))

(def ε 0.01)

(def parts [(translate [(+ ε 200) (+ ε 200) 0]
                       (cube 400 400 400))
            (translate [(+ ε 200) (- -200 ε) 0]
                       (cube 400 400 400))
            (translate [(- -200 ε) (+ ε 200) 0]
                       (cube 400 400 400))
            (translate [(- -200 ε) (- -200 ε) 0]
                       (cube 400 400 400))])

(def bits
  (let [rparts (concat (rest parts) [(first parts)])
        all-bits (apply difference ribbon parts)
        each-bit (for [[p1 p2] [parts rparts]]
                   (intersection all-bits (hull p1 p2)))]
    each-bit))

(spit "things/minktest.scad"
      (write-scad (union (x-pins 10) (x-holes 10) )))
       
