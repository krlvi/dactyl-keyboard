(ns dactyl-keyboard.sides-pieces
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
            [unicode-math.core :refer :all]))

(defn intersects-from-notation [notation]
  "Create intersect shapes to split the marshmallowy sides from the layout's :sides-partitions vector. This vector is of the form [ [[:place x y] ...] ...]. Each :place is :at-k, :at-t, :D-of-k, or :D-of-t, where D is one of the eight compass directions n, s, e, w, nw, sw, ne, se. You are essentially drawing polygons in the 2-D space of the keyboard; at-k or at-t places a vertex at a key-place or thumb-place, respectively; the compass directions place a vertex outside the keyboard in that direction. Some directions, e.g. :ne-of-t, are unimplemented (northeast of the thumb is toward the inside of the keyboard). The polygons you describe are sort of extruded into 3-D, such that they enclose parts of the keyboard's edge. The sides are then split by taking the intersection of the entire sides shape with each successive intersect. Therefore the intersects must add up to enclose the entire sides shape."
  (let [off-top (- (first rows) 1)
        off-bottom (+ (last rows) 1)
        off-left (- (first columns) 3)
        off-right (+ (last columns) 3)
        off-tleft 5
        off-tbottom -4
        at-k (fn [column row]
               (hull
                (->> web-post
                     (translate [0 0 70])
                     (key-place column row))
                (->> web-post
                     (translate [0 0 -70])
                     (key-place column row))))
        at-t (fn [column row]
               (hull
                (->> web-post
                     (translate [0 0 70])
                     (thumb-place column row))
                (->> web-post
                     (translate [0 0 -70])
                     (thumb-place column row))))
        n-of-k (fn [column row]
                 (hull
                  (->> web-post
                       (translate [0 50 40])
                       (key-place column off-top))
                  (->> web-post
                       (translate [0 0 -120])
                       (key-place column off-top))))
        s-of-k (fn [column row]
                 (hull
                  (->> web-post
                       (translate [0 -50 40])
                       (key-place column off-bottom))
                  (->> web-post
                       (translate [0 0 -120])
                       (key-place column off-bottom))))
        funs {:at-k at-k
              :n-of-k n-of-k
              :s-of-k s-of-k
              :e-of-k (fn [column row] (at-k off-right row))
              :w-of-k (fn [column row] (at-k off-left row))
              :ne-of-k (fn [column row] (n-of-k off-right row))
              :nw-of-k (fn [column row] (n-of-k off-left row))
              :se-of-k (fn [column row] (s-of-k off-right row))
              :sw-of-k (fn [column row] (s-of-k off-left row))
              :at-t at-t
              :n-of-t at-t ; unused; incorrectness better than crashing
              :s-of-t (fn [column row] (at-t column off-tbottom))
              :e-of-t at-t ; unused
              :w-of-t (fn [column row] (at-t off-tleft row))
              :nw-of-t at-t ; unused
              :ne-of-t at-t ; unused
              :sw-of-t (fn [column row] (at-t off-tleft off-tbottom))
              :se-of-t at-t ;unused
              }]
    (for [shape notation]
      (apply hull (for [post shape]
                    (apply (funs (first post)) (rest post)))))))


(def sides-slice-intersects (intersects-from-notation sides-partitions))

; this is related to the -5 above in the sides
; function. the -5 results in the sa-cap x and y dimensions being
; multiplied by 105%. if you adjust the above, you need to adjust
; this, so the glue joints end up in the right place relative to the
; top edge of the marshmallowy sides.
(def joint-nudge-out 1.2)

(defn slice-joints-from-notation [notation]
  (let [z sides-downness
        south (fn [shape] (->> shape
                               (translate [0 (+ (* 1/2 mount-height)
                                                joint-nudge-out)
                                           z])
                               (rotate (* τ 1/2) [0 0 1])))
        north (fn [shape] (->> shape
                      (translate [0 (+ (* 1/2 mount-height)
                                       joint-nudge-out)
                                  z])))
        west (fn [shape] (->> shape
                      (rotate (* τ 1/4) [0 0 1])
                      (translate [(- (* -1/2 mount-width)
                                     joint-nudge-out) 0
                                  z])))
        east (fn [shape] shape) ; unimplemented
        gravities {:n north :s south :e east :w west}
        places {:k key-place :t thumb-place}]
    (for [[grav place col row] notation]
      (fn [shape] ((places place)
                   (reify-column col) (reify-row row)
                   ((gravities grav) shape))))))

(def the-sides-slice-joints (slice-joints-from-notation sides-slice-joints))

(defn sides-regions-for [notation]
  (for [[grav place col row] notation]
    (partial-sides sides-flatness
                                sides-downness
                                sides-thickness
                                sides-radius
                                (around-edge-region [place
                                                     (reify-column col)
                                                     (reify-row row)]))))

(def sides-regions (sides-regions-for sides-slice-joints))
