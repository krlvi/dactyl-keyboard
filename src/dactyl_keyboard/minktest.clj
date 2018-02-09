(ns dactyl-keyboard.minktest
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [unicode-math.core :refer :all]))

; https://tauday.com
(def τ (* π 2))

; csg food
(def ε 0.001)

(defn funky-shape [shrink]
  (let [d (- 30 shrink)]
    (union (cube d d d)
                   #_(->> (cube d d d)
                        (rotate (/ τ 4) [1 1 0])
                        (translate [35 20 0])))))
  
(def ribbon (intersection
             (difference (funky-shape 0) (funky-shape 0.01))
             (cube 400 400 0.2)))

(defn gasket-shape [radius]
  (let [diameter (* 2 radius)]
    (binding [*fn* 15] (sphere radius))))

(def gasket (minkowski ribbon (gasket-shape 10)))

(def gasket-shell-radius 8.5)
(def gasket-shell
  (let [little-gasket (minkowski ribbon (gasket-shape gasket-shell-radius))]
    (difference
     (difference gasket little-gasket)
     (funky-shape 0))))

(def npins 3)
(def pin-tolerance 0.5)
(def pin-length 8)
(def pin-fn 8)

(defn x-pin-places [gasket-shape-radius shape]
  (apply union
         (for [pin (range npins)]
           (->> shape
                (rotate (* τ 3/8) [1 0 0])
                (rotate (* -1 (+ 1 pin)
                           (/ (/ τ 2) (+ 1 npins))) [-1 0 0])
                (translate [0 0 (* 0.55 gasket-shape-radius)])
                (rotate (* (+ 1 pin)
                           (/ (/ τ 2) (+ 1 npins))) [-1 0 0])))))

(defn x-half-cylinder [gasket-shape-radius height position]
  (let [bigger (+ 2 (* 2 gasket-shape-radius))
        half (translate [0 (* 1/2 bigger) 0] (cube bigger bigger bigger))
        chop (intersection half (cylinder gasket-shape-radius height))]
    (->> chop
         (rotate (/ τ 4) [0 1 0])
         (translate [(* 1/2 height) 0 0])
         (translate [position 0 0]))))

(defn x-pin-hull [gasket-shape-radius]
  (let [height (* 1/3 pin-length)]
    (x-half-cylinder gasket-shape-radius height (- height))))

(defn x-pin-hull-intersect [gasket-shape-radius]
  (let [height (* 1/3 pin-length)]
    (x-half-cylinder (* 2 gasket-shape-radius) height (- height))))

(defn x-pins [gasket-shape-radius]
  (let [pin-block-height (* 1/3 pin-length)
        tooth-size (* 0.4 gasket-shape-radius)
        pin-block (x-half-cylinder gasket-shape-radius pin-block-height
                                   (- pin-block-height))
        pin (difference
             (->> (cube tooth-size tooth-size tooth-size)
                  (rotate (* τ 1/8) [0 1 0])
                  (rotate (* τ 1/8) [1 0 0])
                  (translate [0 0 0]))
             (->> (cube gasket-shape-radius gasket-shape-radius
                        gasket-shape-radius)
                  (translate [(* -1/2 gasket-shape-radius) 0 0])))
        pins (x-pin-places gasket-shape-radius pin)]
    (union pin-block pins)))

(defn x-holes [gasket-shape-radius]
  (let [hole-block-height (* 1/2 pin-length)
        pin-radius (/ gasket-shape-radius 8)
        hole-block (x-half-cylinder gasket-shape-radius hole-block-height
                                    (+ 0
                                       pin-tolerance))
        hole-tooth-size (+ (* 0.4 gasket-shape-radius) (* 2 pin-tolerance))
        hole (difference
             (->> (cube hole-tooth-size hole-tooth-size hole-tooth-size)
                  (rotate (* τ 1/8) [0 1 0])
                  (rotate (* τ 1/8) [1 0 0])
                  (translate [0 0 0]))
             (->> (cube gasket-shape-radius gasket-shape-radius
                        gasket-shape-radius)
                  (translate [(* -1/2 gasket-shape-radius) 0 0])))
        holes (->> (x-pin-places gasket-shape-radius hole)
                   (translate [pin-tolerance 0 0])
                   (translate [(- ε) 0 0]))]
    (difference hole-block holes)))

(defn x-hole-hull [gasket-shape-radius]
  (let [height (+ (* 1/3 pin-length) pin-tolerance)]
    (x-half-cylinder gasket-shape-radius
                     height
                     (+ (* 1/2 pin-length) pin-tolerance))))

(defn x-hole-hull-intersect [gasket-shape-radius]
  (let [height (+ (* 1/3 pin-length) pin-tolerance)]
    (x-half-cylinder
     (* 2 gasket-shape-radius)
     height
     (+ (* 1/2 pin-length) pin-tolerance))))

(defn x-gap [gasket-shape-radius]
  (x-half-cylinder (* 2 gasket-shape-radius)
                   pin-tolerance
                   (- ε)))

(defn pieces-with-x-pins-and-holes [x-pins-radius
                                    joint-places
                                    intersection-shapes]
  (let [r x-pins-radius
        joint-places-rot1 (concat (rest joint-places)
                                  [(first joint-places)])]
    (for [[jp1 jp2 is] (map vector joint-places
                            joint-places-rot1
                            intersection-shapes)]
      (let [pin-attachment-1
            (hull (jp1 (x-pin-hull r))
                  (intersection
                   gasket-shell
                   (jp1 (x-pin-hull-intersect r))))
            hole-attachment-1
            (hull (jp1 (x-hole-hull r))
                  (intersection
                   gasket-shell
                   (jp1 (x-hole-hull-intersect r))))
            pin-attachment-2
            (hull (jp2 (x-pin-hull r))
                  (intersection
                   gasket-shell
                   (jp2 (x-pin-hull-intersect r))))
            hole-attachment-2
            (hull (jp2 (x-hole-hull r))
                  (intersection
                   gasket-shell
                   (jp2 (x-hole-hull-intersect r))))]
        (union (intersection
                (difference gasket-shell (jp2 (x-gap r))) is)
               pin-attachment-1
               (jp1 (x-pins r))
               hole-attachment-2
               (jp2 (x-holes r)))))))

(def gasket-with-joints-pieces
  (pieces-with-x-pins-and-holes
   gasket-shell-radius
   [(fn [shape] (translate [0 15 0] shape))
    (fn [shape] (->> shape
                     (mirror [1 0 0])
                     (mirror [0 1 0])
                     (translate [0 -15 0])))
    (fn [shape] (->> shape
                     (rotate (- (/ τ 4)) [0 0 1])
                     (translate [15 0 0])))]
   [(translate [-20 0 0] (cube 40 80 40))
    (translate [20 -40 0] (cube 40 80 40))
    (translate [20 40 0] (cube 40 80 40))]))

(doseq [[partno part] (map vector (range) gasket-with-joints-pieces)]
  (spit (format "things/minktest-%02d.scad" partno)
        (write-scad part)))
       
(spit "things/minktest.scad" (write-scad (x-holes 8)#_(union (x-pins 8) (x-holes 8) (x-hole-hull 8))))
