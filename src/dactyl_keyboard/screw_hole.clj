(ns dactyl-keyboard.screw-hole
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [dactyl-keyboard.switch-hole :refer [web-thickness]]
            [unicode-math.core :refer :all]))

;; Heat-Set Inserts for Plastics
;; https://www.mcmaster.com/#94180A331
;; https://www.matterhackers.com/articles/fasteners-for-3d-printing

(def insert-hole-bottom-diameter 5.1)
(def insert-hole-top-diameter 5.31)
(def insert-hole-height 3.8)
(def screw-hole-wall-thickness 1.3)

;; M3 screws.
(def screw-diameter 3)
(def screw-radius (* 1/2 screw-diameter))

;; this would have been (+ plate-thickness insert-hole-height) but
;; they don't sell custom screws, so i did the math and figured a
;; standard size.
;;
;; https://www.mcmaster.com/#90116A153
(def frame-screw-length 8)

(def frame-screw-hole
  (cylinder screw-radius (* 3 web-thickness)))

;; top is at z=0
(def insert-boss
  (let [bottom-radius (* 1/2 insert-hole-bottom-diameter)
        top-radius (* 1/2 insert-hole-top-diameter)]
    (translate [0 0 (* -1/2 insert-hole-height)]
               (cylinder [bottom-radius top-radius] insert-hole-height))))

(def screw-hole-base-diameter 25)
(defn screw-hole-pillar [height]
  (let [thic screw-hole-wall-thickness
        top-r (+ (* 1/2 insert-hole-top-diameter) thic)
        bottom-r (* 1/2 screw-hole-base-diameter)
        power 6 ; determines how sharply the horn curves. should be even.
        a (/ (- bottom-r top-r) (Math/pow (- thic height) power))
        f (fn [y] (+ top-r (* a (Math/pow (- y height) power))))
        steps 5
        step-size (/ height steps)
        horn
        (apply union
               (for [y (range 0 height step-size)]
                 (let [ny (+ y step-size)
                       y-r (f y)
                       ny-r (f ny)]
                   (->> (cylinder [y-r ny-r] step-size)
                        (translate [0 0 (- (+ y (* 1/2 step-size))
                                           height)])))))]
    (difference horn
                (->> insert-boss
                     (color [1 0 0])
                     (translate [0 0 ε])))))

(def screw-hole-pillar-glue-tolerance 0.2)

(defn screw-hole-pillar-splitter [height]
  (let [height-fraction 1/6
        tz (* height-fraction height)
        ty (* 3/2 screw-hole-base-diameter)
        tx (* 3/2 screw-hole-base-diameter)
        pattern-size 8
        all-slices
        (apply union
               (for [ab [0 1]]
                 (apply union
                        (for [slice (range (+ 2 (quot tz pattern-size)))]
                          (call-module "vertical_prisms_slice"
                                       tz ty tx pattern-size (- ε)
                                       ab slice)))))
        
        ]
    (->> all-slices
         (rotate (* 1/4 τ) [0 1 0])
         (translate [0 0 (* 1/2 tz)]))))

(defn screw-hole-pillar-upper [height]
  (difference (screw-hole-pillar height)
              (translate [0 0 (+ (- height)
                                 screw-hole-pillar-glue-tolerance)]
                         (screw-hole-pillar-splitter height))))

(defn screw-hole-pillar-lower [height]
  (intersection (screw-hole-pillar height)
                (translate [0 0 (- height)]
                           (screw-hole-pillar-splitter height))))
