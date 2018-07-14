;; --- BEGIN AGPLv3-preamble ---
;; Dactyl Marshmallow ergonomic keyboard generator
;; Copyright (C) 2015, 2018 Matthew Adereth and Jared Jennings
;;
;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU Affero General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Affero General Public License for more details.
;;
;; You should have received a copy of the GNU Affero General Public License
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.
;; --- END AGPLv3-preamble ---
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
            [dactyl-keyboard.sides :refer :all]
            [dactyl-keyboard.half-circle-connectors :refer :all]
            [unicode-math.core :refer :all]))

(def ^:const sides-connector-radius 8)

;; There isn't room for the whole semicircle of the
;; sides-connector-sides because of the connectors that connect the
;; pieces of the sides to each other. And the sides-connector-frames
;; are not very wide anyway. So we're going to clip the
;; sides-connector-sides to this width.
(def ^:const sides-connector-sides-width sides-connector-radius)


(defn sides-connector-sides-clip [shape]
  (let [big 100]
    (intersection shape (cube big sides-connector-sides-width big))))

(def xu (partial rotate (* -1/4 τ) [1 0 0]))

(defn sides-connector-frame-common [move-outward
                                    turn-outward
                                    move-middle-between-keys
                                    move-key-a
                                    move-key-b
                                    to-edge-distance
                                    web-post-b-ward
                                    web-post-a-ward]
  (let [wedge-factor 0
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

;; This one, unlike the one above, works properly to construct
;; connectors even between rows that are offset relative to each
;; other. But its use of widths and heights makes it only correct in
;; the specific case of north or south frame connectors, despite
;; its "common" name.
(defn sides-connector-frame-common-2 [move-outward
                                      turn-outward
                                      move-middle-between-keys
                                      move-half-a-row-inward-between-keys
                                      move-key-a
                                      move-key-b
                                      rotate-top-inward
                                      to-edge-distance]
  (let [
        block-height (+ (* 2 web-thickness) sides-connector-radius)
                                        ; * 2 because the block starts at
                                        ; the top of the switch hole
        bigger (* 3 block-height)
        move-connector
        (fn [shape]
          (->> shape
               turn-outward
               (move-outward to-edge-distance)
               (translate [0 0 (- plate-thickness)])
               move-middle-between-keys))
        move-block-top-into-web
        #(translate [0 0 (+ (* -1/2 block-height) web-thickness)] %)
        clearance 2 ; horizontal, between hole and side of block
        clearance-cube (cube (+ clearance keyswitch-width)
                             (+ clearance keyswitch-height)
                             bigger)
        more-clearance-cube (cube (+ clearance keyswitch-width ε)
                             (* 3 keyswitch-height)
                             bigger)
        connector-block
        (difference
         (->> (cube keyswitch-width mount-height block-height)
              move-block-top-into-web
              move-middle-between-keys)
         (move-key-a more-clearance-cube)
         (move-key-b more-clearance-cube)
         (->> clearance-cube
              move-half-a-row-inward-between-keys))
        connector-pin-space
        (difference
         (->> (cube keyswitch-width pin-length block-height)
              (move-outward (+ to-edge-distance (* 1/2 pin-length)))
              move-block-top-into-web
              move-middle-between-keys)
         (move-key-a more-clearance-cube)
         (move-key-b more-clearance-cube))
        ]
    (union
     (color [1 0 0] (intersection
         connector-pin-space
         (move-connector (xu (x-solid-pins sides-connector-radius)))))
     (color [0 1 0] connector-block))))


(defn sides-connector-frame-k-s [col1 col2 row]
  (sides-connector-frame-common-2
   #(translate [0 (- %) 0] %2)
   #(rotate (* -1/4 τ) [0 0 1] %)
   #(key-place (* 1/2 (+ col1 col2)) row %)
   #(key-place (* 1/2 (+ col1 col2)) (- row 1/2) %)
   #(key-place col1 row %)
   #(key-place col2 row %)
   #(rotate (- %) [1 0 0] %2)
   (* 1/2 mount-height)))

(defn sides-connector-frame-k-n [col1 col2 row]
  (sides-connector-frame-common-2
   #(translate [0 % 0] %2)
   #(rotate (* 1/4 τ) [0 0 1] %)
   #(key-place (* 1/2 (+ col1 col2)) row %)
   #(key-place (* 1/2 (+ col1 col2)) (+ row 1/2) %)
   #(key-place col1 row %)
   #(key-place col2 row %)
   #(rotate % [1 0 0] %2)
   (* 1/2 mount-height)))

;; thumb hacks! the rows go backward in the thumb

(defn sides-connector-frame-t-s [col1 col2 row]
  (sides-connector-frame-common-2
   #(translate [0 (- %) 0] %2)
   #(rotate (* -1/4 τ) [0 0 1] %)
   #(thumb-place (* 1/2 (+ col1 col2)) row %)
   #(thumb-place (* 1/2 (+ col1 col2)) (+ row 1/2) %)
   #(thumb-place col1 row %)
   #(thumb-place col2 row %)
   #(rotate (- %) [1 0 0] %2)
   (* 1/2 mount-height)))

(defn sides-connector-frame-t-n [col1 col2 row]
  (sides-connector-frame-common-2
   #(translate [0 % 0] %2)
   #(rotate (* 1/4 τ) [0 0 1] %)
   #(thumb-place (* 1/2 (+ col1 col2)) row %)
   #(thumb-place (* 1/2 (+ col1 col2)) (- row 1/2) %)
   #(thumb-place col1 row %)
   #(thumb-place col2 row %)
   #(rotate % [1 0 0] %2)
   (* 1/2 mount-height)))

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
        sides-hull-space (->> (x-half-cylinder
                           [sides-connector-radius (* 3 sides-connector-radius)]
                           (* 4 sides-connector-radius)
                           0)
                              xu
                              move-connector)
        to-holes (hull (intersection sides-hull-space sides-shape)
                       (->> (x-solid-holes-for-hull sides-connector-radius)
                            xu
                            sides-connector-sides-clip
                            move-connector))
        the-holes (->> (x-solid-holes sides-connector-radius)
                       xu
                       sides-connector-sides-clip
                       move-connector)
        ]
    (union to-holes the-holes)))

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


(defn sides-connectors-frame-from-notation [notation & rest]
  (let [gravities {:n sides-connector-frame-k-n
                   :s sides-connector-frame-k-s
                   :e sides-connector-frame-e
                   :w sides-connector-frame-w}
        places {:k key-place :t thumb-place}]
    (for [cols columns-pieces]
                                        ; notation is split into
                                        ; multiple vectors by sides
                                        ; piece, but we are emitting
                                        ; these by frame piece
      (apply union
             (for [[grav place one two three] (apply concat notation)]
               #_((gravities grav) (places place) one two three)
               (cond (and
                      (= place :k)
                      (some (partial = grav) [:e :w]) ; one is the constant column
                      (some (partial = one) cols))
                     (do
                       (print (format "scffn : columns %s; params %s\n"
                                      (str cols) (str [grav place one two three])))
                       (apply (gravities grav) (places place) one two three rest))
                     (and
                      (= place :k)
                      (some (partial = grav) [:n :s]) ; one and two are the columns
                      (some (partial = one) cols)
                      (some (partial = two) cols))
                     (do
                       (print (format "scffn : columns %s; params %s\n"
                                      (str cols) (str [grav place one two three])))
                       (apply (gravities grav) one two three rest))))))))

;; this is pretty duplicative with the above, continuing the theme
;; that the thumb is a hack.
(defn sides-connectors-thumb-from-notation [notation & rest]
  (let [gravities {:n sides-connector-frame-t-n
                   :s sides-connector-frame-t-s
                   :e sides-connector-frame-e
                   :w sides-connector-frame-w}
        places {:k key-place :t thumb-place}]
    (apply union
           (for [[grav place one two three] (apply concat notation)]
             (cond (and
                    (= place :t)
                    (some (partial = grav) [:e :w])) ; one is the constant column
                   (do
                     (print (format "sctfn : params %s\n"
                                    (str [grav place one two three])))
                                        ; rows go the opposite direction in the thumb
                     (apply (gravities grav) (places place) one three two rest))
                   (and
                    (= place :t)
                    (some (partial = grav) [:n :s])) ; one and two are the columns
                   (do
                     (print (format "sctfn : params %s\n"
                                    (str [grav place one two three])))
                     (apply (gravities grav) one two three rest)))))))

(defn sides-connectors-sides-from-notation [notation shape]
  (let [gravities {:n sides-connector-sides-n
                   :s sides-connector-sides-s
                   :e sides-connector-sides-e
                   :w sides-connector-sides-w}
        places {:k key-place :t thumb-place}]
                                        ; notation is split into
                                        ; multiple vectors by sides
                                        ; piece
    (for [[pieceno piece] (map vector (range) notation)]
      (apply union
             (for [[grav place one two three] piece]
               (do
                 (print (format "scsfn : piece %d; params %s\n"
                                pieceno (str [grav place one two three])))
                 (let [pertinent-shell-notation
                       (cond (some (partial = grav) [:e :w])
                             ; two and three are the rows
                             [[grav place one two] [grav place one three]]
                             (some (partial = grav) [:n :s])
                                        ; one and two are the columns
                             [[grav place one three] [grav place two three]])
                       pertinent-shell (partial-sides
                                        pertinent-shell-notation)]
                   ((gravities grav) (places place) one two three
                    pertinent-shell))))))))
