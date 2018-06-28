(ns dactyl-keyboard.teensy
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [dactyl-keyboard.placement :refer :all]
            [dactyl-keyboard.layout :refer [key-place-fn teensy-bracket-at]]
            [unicode-math.core :refer :all]))

;; https://www.pjrc.com/teensy/dimensions.html
(def teensy-board-width 18)
(def teensy-board-height 3)
(def teensy-board-length 36)

(def teensy-pcb-thickness 1.6)
(def teensy-offset-height 5)


(def teensy-pcb (->> (cube teensy-board-width teensy-board-length teensy-pcb-thickness)
                     (translate [0 0 (+ (/ teensy-pcb-thickness -2) (- teensy-offset-height))])
                     (key-place 1/2 3/2)
                     (color [1 0 0])))

(def teensy-place (key-place-fn teensy-bracket-at))
(def teensy-support
  (union
   (union
    (->> (cube 3 3 9)
         (translate [0 0 -2])
         (key-place 1/2 3/2)
         (color [0 1 0]))
    (hull (->> (cube 3 6 9)
               (translate [0 0 -2])
               (key-place 1/2 2)
               (color [0 0 1]))
          (->> (cube 3 3 (+ teensy-pcb-thickness 3))
               (translate [0 (/ teensy-board-length -2) (+ (- teensy-offset-height)
                                            #_(/ (+ teensy-pcb-thickness 3) -2)
                                            )])
               teensy-place
               (color [0 0 1]))))
   teensy-pcb
   (->> (cube teensy-board-width teensy-board-length teensy-pcb-thickness)
        (translate [0 1.5 (+ (/ teensy-pcb-thickness -2) (- teensy-offset-height) -1)])
        teensy-place
        (color [1 0 0]))))

