(ns dactyl-keyboard.sides-connectors
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [dactyl-keyboard.switch-hole :refer :all]
            [dactyl-keyboard.keycaps :refer :all]
            [dactyl-keyboard.placement :refer :all]
            [dactyl-keyboard.layout :refer :all]
            [dactyl-keyboard.connectors :refer :all]
            [dactyl-keyboard.marshmallowy-sides :refer :all]
            [dactyl-keyboard.half-circle-connectors :refer :all]
            [unicode-math.core :refer :all]))

(def ^:const sides-connector-radius 8)

(defn sides-connector-frame-ew [out-x-direction sides-object place column row1 row2]
  "place is a placement function. connector will be between row1 and row2 at column.
   out-x-direction is either 1 or -1, depending on whether column is on the east or
   west side of the board."
  (let [turn-amount (if (= out-x-direction -1) (* 1/2 τ) 0)
        vertical-only-factor 2/3
        vertical-only-distance (* 1/2 mount-width vertical-only-factor)
        translate-outside-x
        #(translate [(* 1/2 out-x-direction mount-width) 0 0] %)
        rotate-outside
        #(rotate turn-amount [0 0 1] %)
        place-between #(place column (/ (+ row1 row2) 2) %)
        place-above #(place column row1 %)
        place-below #(place column row2 %)
        place-connector (fn [shape]
                          (->> shape
                               rotate-outside
                               translate-outside-x
                               (translate [0 0 (* -1 plate-thickness)])
                               place-between))
        frame-triangle-face
        (hull
         (->> web-post-b
              (translate [vertical-only-distance 0 (- plate-thickness)])
              place-above)
         (->> web-post-t
              (translate [vertical-only-distance 0 (- plate-thickness)])
              place-below)
         (->> web-post-b
              (translate [vertical-only-distance 0 (- 0 plate-thickness sides-connector-radius)])
              place-above)
         (->> web-post-t
              (translate [vertical-only-distance 0 (- 0 plate-thickness sides-connector-radius)])
              place-below))
        frame-triangle (hull (place-above web-post-b)
                             (place-below web-post-t)
                             frame-triangle-face)]
    (difference
     (union
      frame-triangle
      (hull (place-connector (xu-pin-block-for-hull sides-connector-radius))
            frame-triangle-face)
      (place-connector (xu-pins sides-connector-radius)))
     ; this is the holes with no tolerance
     #_(xu-hollow-pins sides-connector-radius 0.9 0.85
                            (+ pin-length (* 2 interface-thickness))
                            pin-length
                            xu-pin-block))))
