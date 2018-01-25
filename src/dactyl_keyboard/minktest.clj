(ns dactyl-keyboard.minktest
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [unicode-math.core :refer :all]))

(def τ (* π 2))

(defn funky-shape [shrink]
  (let [d (- 100 shrink)]
    (rotate (/ τ 20) [0 0 1]
            (union (cube d d d)
                   (->> (cube d d d)
                        (rotate (/ τ 4) [1 1 0])
                        (translate [35 20 0]))))))
  
(def ribbon (intersection
            (difference (funky-shape 0) (funky-shape 2))
            (cube 400 400 0.2)))

(defn gasket-shape [radius]
  (let [diameter (* 2 radius)]
    (->> (binding [*fn* 4] (cube diameter diameter diameter)))
         (rotate (/ τ 8) [0 0 1])
         (rotate (/ τ 8) [1 0 0])))
(def gasket (minkowski ribbon (gasket-shape 10)))

(def gasket-shell
  (let [little-gasket (minkowski ribbon (gasket-shape 8))]
    (difference
     (difference gasket little-gasket)
     (funky-shape 0))))

(def parts [(translate [200 200 0] (cube 400 400 400))
            (translate [200 -200 0] (cube 400 400 400))
            (translate [-200 200 0] (cube 400 400 400))
            (translate [-200 -200 0] (cube 400 400 400))])

(def npins 3)
(def pin-tolerance 0.5)
(def pin-length 9)
(defn x-pins [gasket-shape-radius]
  (let [pin-block-height (* 1/3 pin-length)
        pin-radius (/ gasket-shape-radius 4)]
    (union
     (->> (cylinder gasket-shape-radius pin-block-height)
          (rotate (/ τ 4) [0 1 0])
          (translate [(* 1/2 pin-block-height) 0 0]))
     (binding [*fn* 8]
       (apply union
              (for [pin (range npins)]
                (->> (cylinder pin-radius pin-length)
                     (rotate (/ τ 4) [0 1 0])
                     (translate [0 0 (/ gasket-shape-radius 2)])
                     (rotate (* pin (/ τ npins)) [1 0 0])
                     (translate [(* 1/2 pin-length) 0 0]))))))))
  
  (defn x-holes [gasket-shape-radius]
    (let [hole-block-height (- (* 2/3 pin-length) pin-tolerance)
          pin-radius (/ gasket-shape-radius 4)]
      (difference
       (->> (cylinder gasket-shape-radius hole-block-height)
            (rotate (/ τ 4) [0 1 0])
            (translate [(/ hole-block-height 2) 0 0])
            (translate [(- pin-length hole-block-height) 0 0]))
       (binding [*fn* 8]
         (apply union
                (for [pin (range npins)]
                                        ; * 1.2: comfortably longer than block so difference is clean
                  (->> (cylinder (+ (/ gasket-shape-radius 5) pin-tolerance) (* pin-length 1.2))
                       (rotate (/ τ 4) [0 1 0])
                       (translate [0 0 (/ gasket-shape-radius 2)])
                       (rotate (* pin (/ τ npins)) [1 0 0])
                       (translate [(/ pin-length 2) 0 0]))))))))

(spit "things/minktest.scad"
      (write-scad (union (x-holes 8) (x-pins 8))))
       
