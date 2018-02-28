(ns dactyl-keyboard.marshmallowy-sides-pieces
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [dactyl-keyboard.switch-hole :refer :all]
            [dactyl-keyboard.keycaps :refer :all]
            [dactyl-keyboard.placement :refer :all]
            [dactyl-keyboard.layout :refer :all]
            [dactyl-keyboard.connectors :refer :all]
            [unicode-math.core :refer :all]))

(def marshmallow-slice-intersects
  (let [off-top (- (first rows) 1)
        off-bottom (+ (last rows) 1)
        off-left (- (first columns) 3)
        off-right (+ (last columns) 3)
        top-post (fn [column]
                   (hull
                    (->> web-post
                         (translate [0 50 40])
                         (key-place column off-top))
                    (->> web-post
                         (translate [0 0 -120])
                         (key-place column off-top))))
        bottom-post (fn [column]
                      (hull
                       (->> web-post
                            (translate [0 -50 40])
                            (key-place column off-bottom))
                       (->> web-post
                            (translate [0 0 -120])
                            (key-place column off-bottom))))
        right-split 4
        right-slice (hull (top-post right-split) (bottom-post right-split)
                          (top-post off-right) (bottom-post off-right))
        post-at (fn [column row]
                  (hull
                   (->> web-post
                        (translate [0 0 70])
                        (key-place column row))
                   (->> web-post
                        (translate [0 0 -70])
                        (key-place column row))))
        top-split 1
        top-slice (hull (top-post right-split)
                        (post-at right-split top-split)
                        (post-at off-left top-split)
                        (post-at off-left off-top))
        thumb-post (fn [column row]
                     (hull
                      (->> web-post
                           (translate [0 0 70])
                           (thumb-place column row))
                      (->> web-post
                           (translate [0 0 -70])
                           (thumb-place column row))))
        off-tleft 5
        thumb-lsp 0
        left-slice (hull (post-at off-left top-split)
                         (thumb-post off-tleft thumb-lsp)
                         (thumb-post 0 thumb-lsp)
                         (post-at 0 top-split))
        off-tbottom -4
        lower-left-slice (hull (thumb-post 1 thumb-lsp)
                               (thumb-post off-tleft thumb-lsp)
                               (thumb-post off-tleft off-tbottom)
                               (thumb-post 1 off-tbottom))
        remainder (hull (thumb-post 1 thumb-lsp)
                         (thumb-post 1 off-tbottom)
                         (bottom-post right-split)
                         (post-at right-split top-split))]
    [(color [1 0 0] right-slice)
     (color [1 1 0] top-slice)
     (color [0 1 1] left-slice)
     (color [1 0 1] lower-left-slice)
     (color [0 0 1] remainder)]))

; this is related to the -5 above in the big-marshmallowy-sides
; function. the -5 results in the sa-cap x and y dimensions being
; multiplied by 105%. if you adjust the above, you need to adjust
; this, so the glue joints end up in the right place relative to the
; top edge of the marshmallowy sides.
(def joint-nudge-out 1.2)
(def marshmallow-slice-joints
  (let [right-split 4
        top-split 1
        thumb-lsp 0]
    [(fn [shape] (->> shape
                      (translate [0 (+ (* 1/2 mount-height)
                                       joint-nudge-out)
                                  (- web-thickness)])
                      (rotate (* τ 1/2) [0 0 1])
                      (key-place right-split (last rows))))
     (fn [shape] (->> shape
                      (translate [0 (+ (* 1/2 mount-height)
                                       joint-nudge-out)
                                  (- web-thickness)])
                      (key-place right-split (first rows))))
     (fn [shape] (->> shape
                      (rotate (* τ 1/4) [0 0 1])
                      (translate [(- (* -1/2 mount-width)
                                     joint-nudge-out) 0
                                  (- web-thickness)])
                      (key-place (first columns) top-split)))
     (fn [shape] (->> shape
                      (rotate (* τ 1/4) [0 0 1])
                      (translate [(- (* -1/2 mount-width)
                                     joint-nudge-out) 0
                                  (- web-thickness)])
                      (thumb-place 2 thumb-lsp)))
     (fn [shape] (->> shape
                      (translate [0 (+ (* 1/2 mount-height)
                                       joint-nudge-out)
                                  (- web-thickness)])
                      (rotate (* τ 1/2) [0 0 1])
                      (thumb-place 1 -1)))]))
