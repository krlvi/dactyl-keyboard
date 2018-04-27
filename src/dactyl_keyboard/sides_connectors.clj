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


(def xu (partial rotate (* -1/4 τ) [1 0 0]))

(defn sides-connector-frame-common [move-outward
                                    turn-outward
                                    move-middle-between-keys
                                    move-key-a
                                    move-key-b
                                    to-edge-distance
                                    web-post-b-ward
                                    web-post-a-ward]
  (let [wedge-factor 2/3
        move-connector
        (fn [shape]
          (->> shape
               turn-outward
               (move-outward to-edge-distance)
               (translate [0 0 (- plate-thickness)])
               move-middle-between-keys))
        frame-triangle-face-at
        (fn [outward-amount]
          (hull
           (->> web-post-b-ward
                (move-outward outward-amount)
                (translate [0 0 (* -2/3 web-thickness)])
                move-key-a)
           (->> web-post-a-ward
                (move-outward outward-amount)
                (translate [0 0 (* -2/3 web-thickness)])
                move-key-b)
           (->> web-post-b-ward
                (move-outward outward-amount)
                (translate [0 0 (- 0 web-thickness sides-connector-radius)])
                move-key-a)
           (->> web-post-a-ward
                (move-outward outward-amount)
                (translate [0 0 (- 0 web-thickness sides-connector-radius)])
                move-key-b)))
        connector-block
        (union
         (hull (move-key-a web-post-b-ward)
               (move-key-b web-post-a-ward)
               (frame-triangle-face-at (* wedge-factor to-edge-distance)))
         (hull (frame-triangle-face-at (* wedge-factor to-edge-distance))
               (frame-triangle-face-at to-edge-distance)))
        connector-pin-space
        (hull (frame-triangle-face-at to-edge-distance)
              (frame-triangle-face-at (+ to-edge-distance
                                         pin-length)))
        pins
        (intersection
         connector-pin-space
         (move-connector (xu (x-solid-pins sides-connector-radius))))]
    (union connector-block pins)))

(defn sides-connector-frame-x [out-x-direction place column row1 row2]
  "place is a placement function. connector will be between row1 and row2 at column.
   out-x-direction is either 1 or -1, depending on whether column is on the east or
   west side of the board."
  ; a-ward is +y (upward in key row space); b-ward is -y (downward)
  (sides-connector-frame-common
   #(translate [(* out-x-direction %) 0 0] %2)
   (if (= out-x-direction -1)
     #(rotate (* 1/2 τ) [0 0 1] %)
     #(rotate 0 [0 0 1] %))
   #(place column (/ (+ row1 row2) 2) %)
   #(place column row1 %)
   #(place column row2 %)
   (* 1/2 mount-width)
   web-post-b
   web-post-t))

(def sides-connector-frame-e 
  (partial sides-connector-frame-x 1))
(def sides-connector-frame-w 
  (partial sides-connector-frame-x -1))

(defn sides-connector-frame-y [out-y-direction place column1 column2 row]
  "place is a placement function. connector will be between column1 and column2 at row.
   out-y-direction is either 1 or -1, depending on whether column is on the north or
   south side of the board."
  ; a-ward is -x (left in key row space); b-ward is +x (right)
  (sides-connector-frame-common
   #(translate [0 (* out-y-direction %) 0] %2)
   (if (= out-y-direction -1)
     #(rotate (* 3/4 τ) [0 0 1] %)
     #(rotate (* 1/4 τ) [0 0 1] %))
   #(place (/ (+ column1 column2) 2) row %)
   #(place column2 row %)
   #(place column1 row %)
   (* 1/2 mount-height)
   web-post-l
   web-post-r))

(def sides-connector-frame-n 
  (partial sides-connector-frame-y 1))
(def sides-connector-frame-s 
  (partial sides-connector-frame-y -1))



(defn sides-connector-sides-common [move-outward
                                    turn-outward
                                    move-middle-between-keys
                                    move-key-a
                                    move-key-b
                                    to-edge-distance
                                    web-post-b-ward
                                    web-post-a-ward
                                    sides-shape]
  (let [
        move-connector
        (fn [shape]
          (->> shape
               turn-outward
               (move-outward to-edge-distance)
               (translate [0 0 (- plate-thickness)])
               move-middle-between-keys))
        sides-hull-space (move-connector
                          (xu (x-half-cylinder
                           [sides-connector-radius (* 3 sides-connector-radius)]
                           (* 4 sides-connector-radius)
                           0)))
        to-holes (hull (intersection sides-hull-space sides-shape)
                       (move-connector (xu (x-solid-holes-for-hull sides-connector-radius))))
        ]
    (union to-holes (move-connector (xu (x-solid-holes sides-connector-radius))))))

(defn sides-connector-sides-x [out-x-direction place column row1 row2 shape]
  "place is a placement function. connector will be between row1 and row2 at column.
   out-x-direction is either 1 or -1, depending on whether column is on the east or
   west side of the board."
  ; a-ward is +y (upward in key row space); b-ward is -y (downward)
  (sides-connector-sides-common
   #(translate [(* out-x-direction %) 0 0] %2)
   (if (= out-x-direction -1)
     #(rotate (* 1/2 τ) [0 0 1] %)
     #(rotate 0 [0 0 1] %))
   #(place column (/ (+ row1 row2) 2) %)
   #(place column row1 %)
   #(place column row2 %)
   (* 1/2 mount-width)
   web-post-b
   web-post-t
   shape))

(def sides-connector-sides-e
  (partial sides-connector-sides-x 1))
(def sides-connector-sides-w
  (partial sides-connector-sides-x -1))

(defn sides-connector-sides-y [out-y-direction place column1 column2 row shape]
  "place is a placement function. connector will be between column1 and column2 at row.
   out-y-direction is either 1 or -1, depending on whether column is on the north or
   south side of the board."
  ; a-ward is -x (left in key row space); b-ward is +x (right)
  (sides-connector-sides-common
   #(translate [0 (* out-y-direction %) 0] %2)
   (if (= out-y-direction -1)
     #(rotate (* 3/4 τ) [0 0 1] %)
     #(rotate (* 1/4 τ) [0 0 1] %))
   #(place (/ (+ column1 column2) 2) row %)
   #(place column2 row %)
   #(place column1 row %)
   (* 1/2 mount-height)
   web-post-l
   web-post-r
   shape))

(def sides-connector-sides-n
  (partial sides-connector-sides-y 1))
(def sides-connector-sides-s
  (partial sides-connector-sides-y -1))


(defn sides-connectors-from-notation [gravities notation & rest]
  (let [places {:k key-place :t thumb-place}]
    (for [cols columns-pieces]
                                        ; notation is split into
                                        ; multiple vectors by sides
                                        ; piece, but we are emitting
                                        ; these by frame piece
      (apply union
             (for [[grav place one two three] (apply concat notation)]
               #_((gravities grav) (places place) one two three)
               (do
                 (print (format "scffn : columns %s; params %s\n"
                                (str cols) (str [grav place one two three])))
                 (cond (and
                        (= place :k)
                        (some (partial = grav) [:e :w]) ; one is the constant column
                        (some (partial = one) cols))
                       (apply (gravities grav) (places place) one two three rest)
                       (and
                        (= place :k)
                        (some (partial = grav) [:n :s]) ; one and two are the columns
                        (some (partial = one) cols)
                        (some (partial = two) cols))
                       (apply (gravities grav) (places place) one two three rest)
                       :else (print (format " -- no call made\n")))))))))

(def sides-connectors-frame-from-notation
  (partial sides-connectors-from-notation
           {:n sides-connector-frame-n
            :s sides-connector-frame-s
            :e sides-connector-frame-e
            :w sides-connector-frame-w}))

(def sides-connectors-sides-from-notation
  (partial sides-connectors-from-notation
           {:n sides-connector-sides-n
            :s sides-connector-sides-s
            :e sides-connector-sides-e
            :w sides-connector-sides-w}))
