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
(ns dactyl-keyboard.dactyl
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [dactyl-keyboard.switch-hole :refer :all]
            [dactyl-keyboard.keycaps :refer :all]
            [dactyl-keyboard.placement :refer :all]
            [dactyl-keyboard.layout :refer :all]
            [dactyl-keyboard.connectors :refer :all]
            [dactyl-keyboard.sides-connectors :refer :all]
            [dactyl-keyboard.frame-glue-joint :refer :all]
            [dactyl-keyboard.sides :refer :all]
            [dactyl-keyboard.bottom :refer :all]
            [dactyl-keyboard.sides-pieces :refer :all]
            [dactyl-keyboard.screw-hole :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.half-circle-connectors :refer :all]
            [dactyl-keyboard.adafruit-usb :refer :all]
            [dactyl-keyboard.rj11 :refer :all]))




(def thumb
  (union
   thumb-connectors
   (thumb-layout (rotate (/ π 2) [0 0 1] chosen-single-plate))
   (thumb-place 0 -1/2 double-plates)
   (thumb-place 1 -1/2 double-plates)))

(def thumb-blanks
  (union
   (thumb-layout (rotate (/ π 2) [0 0 1] chosen-blank-single-plate))
   (thumb-place 0 -1/2 double-plates-blank)
   (thumb-place 1 -1/2 double-plates-blank)))




;;;;;;;;;;;;;;;;;;;;;;;;;
;; Glue Joints for top ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(def glue-post (->> (cube post-size post-size glue-joint-height)
                   (translate [0 0 (+ (/ glue-joint-height -2)
                                      plate-thickness)])))
(def glue-post-t  (translate [0 (- (/ mount-height 2) post-adj) 0] glue-post))
(def glue-post-b  (translate [0 (+ (/ mount-height -2) post-adj) 0] glue-post))

(defn fingers-to-thumb-glue-joints-for-columns [columns]
  (apply union
         (for [[column row] [ [-1 3] [1 4] ] :when (some (partial = column) columns)]
           (union
            (key-place (- column 1/2) row
                       (color [1 0 0] glue-joint-center-left))
            (color [1 0 1]
                   (hull (key-place column row web-post-tl)
                         (key-place (- column 1/2) row
                                    (translate [(/ glue-joint-wall-thickness 2) 0 0]
                                               glue-post-t))
                         (key-place column row web-post-bl)
                         (key-place (- column 1/2) row
                                    (translate [(/ glue-joint-wall-thickness 2) 0 0]
                                               glue-post-b))))))))

                                        ; thumb-glue-joints doesn't
                                        ; get the same loopy
                                        ; treatment, because the 2x1
                                        ; key is different
(def thumb-to-fingers-glue-joints
  (union

   (key-place 1/2 4 (color [0 1 0] glue-joint-center-right))
   (color [1 0 1] (hull (thumb-place 0 -1/2 (translate [0 (/ mount-height 2) 0] web-post-tr)) (key-place 1/2 4 (translate [(- 0 (* glue-joint-wall-thickness 3/2)) 0 0] glue-post-t))
                        (thumb-place 0 -1/2 (translate [0 (/ mount-height 2) 0] web-post-br)) (key-place 1/2 4 (translate [(- 0 (* glue-joint-wall-thickness 3/2)) 0 0] glue-post-b))))

   (key-place -3/2 3 (color [0 1 0] glue-joint-center-right))
   (color [1 0 1] (hull (thumb-place 2 1 web-post-tr) (key-place -3/2 3 (translate [(- 0 (* 3/2 glue-joint-wall-thickness)) 0 0] glue-post-t))
                        (thumb-place 2 1 web-post-br) (key-place -3/2 3 (translate [(- 0 (* 3/2 glue-joint-wall-thickness)) 0 0] glue-post-b))))
   ;; this below is a block so you can clamp this joint when you glue it.
   (color [0 0 1] (hull (->> glue-post-t
                             (translate [(* -7 glue-joint-wall-thickness) 0 0])
                             (key-place -3/2 3))
                        (->> glue-post-b
                             (translate [(* -7 glue-joint-wall-thickness) 0 0])
                             (key-place -3/2 3))
                        (->> glue-post-t
                             (translate [(* -1 glue-joint-wall-thickness) 0 0])
                             (key-place -3/2 3))
                        (->> glue-post-b
                             (translate [(* -1 glue-joint-wall-thickness) 0 0])
                             (key-place -3/2 3))))))

(def right-glue-joints-for-fingerpieces
  (let [rgj-for-this
        (fn [leftmostp rightmostp columns]
          (if (not rightmostp)
            (let [column (last columns)
                  joint-column (+ column 1/2)
                  other-way (- column 1/2)]
              (apply union
                     (for [row rows :when (not (or (and (= column 0) (= row 4))
                                                   (and (= column -1) (= row 4))))]
                       (union
                                        ; the actual paddle
                        (key-place joint-column row
                                   (color [0 1 0] glue-joint-center-right))
                                        ; connect paddle to key-place
                        (color [1 0 1]
                               (hull
                                (key-place column row web-post-tr)
                                (key-place column row (translate [(- (* cherry-bezel-width 1/2)) 0 0] web-post-tr))
                                (key-place joint-column row
                                           (translate [(- 0 (* glue-joint-wall-thickness 3/2)) 0 0]
                                                      glue-post-t))
                                (key-place joint-column row
                                           (translate [(- 0 (* glue-joint-wall-thickness 2/2)) 0 0]
                                                      glue-post-t))
                                (key-place column row web-post-br)
                                (key-place column row (translate [(- (* cherry-bezel-width 1/2)) 0 0] web-post-br))
                                (key-place joint-column row
                                           (translate [(- 0 (* glue-joint-wall-thickness 2/2)) 0 0]
                                                      glue-post-b))
                                (key-place joint-column row
                                           (translate [(- 0 (* glue-joint-wall-thickness 3/2)) 0 0]
                                                      glue-post-b))))))))))]
    (map #_(fn [a b c] ()) rgj-for-this
         (cons true (repeat false))
         (concat (repeat (- (count columns-pieces) 1) false) '(true))
         columns-pieces)))

(def left-glue-joints-for-fingerpieces
  (let [per-column-web-post-transform
        (fn [column]
                                        ; what these should be depends on the column; see key-place above
          (cond
                                        ; these columns go up relative to the previous
            (and (>= column 3) (< column 5))
            (fn [post] (translate [1.3 0 0] post))
                                        ; this column goes down relative to the previous
            (and (>= column 2) (< column e))
            (fn [post] post)
            :else (fn [post] post)))
        lgj-for-this
        (fn [leftmostp rightmostp columns]
          (if (not leftmostp)
            (let [column (first columns)
                  joint-column (- column 1/2)
                  other-way (+ column 1/2)]
              (apply union
                     (for [row rows :when (not (or (and (= column 1) (= row 4))
                                                   (and (= column 0) (= row 4))))]
                       (union
                                        ; the actual paddle
                        (key-place joint-column row
                                   (color [1 0 0] glue-joint-center-left))
                                        ; connect paddle to key-place
                        (color [1 0 1]
                               (hull
                                (key-place joint-column row glue-post-t)
                                (key-place joint-column row (translate [(* glue-joint-wall-thickness 1/2) 0 0] glue-post-t))
                                (key-place column row web-post-tl)
                                (key-place column row (translate [(* cherry-bezel-width 1/2) 0 0] web-post-tl))
                                (key-place joint-column row glue-post-b)
                                (key-place joint-column row (translate [(* glue-joint-wall-thickness 1/2) 0 0] glue-post-b))
                                (key-place column row web-post-bl)
                                (key-place column row (translate [(* cherry-bezel-width 1/2) 0 0] web-post-bl))))))))))]
    (map #_(fn [a b c] ()) lgj-for-this
         (cons true (repeat false))
         (concat (repeat (- (count columns-pieces) 1) false) '(true))
         columns-pieces)))
;;;;;;;;;;;;;;;;;;
;; Final Export ;;
;;;;;;;;;;;;;;;;;;

(defn dactyl-top-right-plusses [key-pieces]
  ; agh i made bad names and now i pay for it
  (let [pieces-of-pieces (map vector
                              (map fingers-to-thumb-glue-joints-for-columns columns-pieces)
                              right-glue-joints-for-fingerpieces
                              key-pieces
                              connectors-inside-fingerpieces
                              left-glue-joints-for-fingerpieces
                              (for [cols columns-pieces]
                                (let [teensy-column (nth teensy-bracket-at 1)]
                                  (if (and (>= teensy-column (first cols))
                                           (<= teensy-column (last cols)))
                                    (->> screw-hole-for-teensy
                                         ((key-place-fn teensy-bracket-at)))))))]
    (for [pieces-of-this-piece pieces-of-pieces]
      (apply union pieces-of-this-piece))))

(defn dactyl-top-right-minuses [key-pieces]
  (let [pieces-of-pieces (map vector
                              screw-holes-in-fingerpieces)]
    (for [pieces-of-this-piece pieces-of-pieces]
      (apply union pieces-of-this-piece))))

(defn dactyl-top-right-pieces [key-pieces]
  (for [[plus minus] (map vector
                          (dactyl-top-right-plusses key-pieces)
                          (dactyl-top-right-minuses key-pieces))]
        (difference plus minus)))

(def dactyl-top-right-thumb
  (difference
   (union thumb thumb-to-fingers-glue-joints)
   screw-holes-in-thumb))

(def define-sides-with-right-ports
  (define-module "SidesWithRightPorts"
    (with-fn 12
      (difference
       (union sides-right
              usb-nice-plate
              rj11-nice-plate)
       (usb-cutout-place adafruit-usb-cutout)
       (rj11-cutout-place rj11-cutout)))))

(def define-sides-with-left-ports
  (define-module "SidesWithLeftPorts"
    (with-fn 12
      (difference
       (union sides-right
              rj11-nice-plate)
       (rj11-cutout-place rj11-cutout)))))


;; Put some keywords in skip-tags, and say-spit calls with those tags
;; will not be evaluated. If you temporarily don't care about some
;; parts, this may help you iterate faster. Usually this should be
;; #{}.
(def skip-tags #{})
;; Put some keywords in emit-tags, and only say-spit calls with those
;; tags will be evaluated. If you are iterating on just one or two
;; parts, this may help you iterate faster. Usually this should be
;; #{}.
(def emit-tags #{})

(defn emit? [tags]
  (cond
    (some emit-tags tags) true
    (and (empty? emit-tags)
         (not-any? skip-tags tags)) true
    :else false))

(defn make-filename [tags]
  (let [tag-abbrevs {:debugmodel "debug-"
                     :piece "dm-"
                     :frame "fra-"
                     :bottom "bot-"
                     :sides "sid-"
                     :legs "leg-"
                     :right "r"
                     :left "l"
                     :thumb "th"}
        stringify (fn [x] (cond
                            (integer? x) (format "%02d" x)
                            (keyword? x) (or (tag-abbrevs x) (name x))))
        filename (format "things/%s.scad"
                         (clojure.string/join (map stringify tags)))]
    filename))

(defmacro say-spit [tags & body]
  (if (emit? tags)
    `(let [filename# (make-filename ~tags)]
       (do
         (print (format "%s  emitting  %s\n" ~tags filename#))
         (spit filename# ~@body)))
    `(print (format "%s *SKIPPING* %s\n" ~tags (make-filename ~tags)))))

(say-spit [:debugmodel :single-plate]
      (write-scad chosen-single-plate))

(say-spit [:piece :frame :right :thumb]
          (write-scad
           (use "key-place.scad")
           (use "eggcrate.scad")
           dactyl-top-right-thumb
           (sides-connectors-thumb-from-notation sides-frame-joints)))

(say-spit [:piece :frame :left :thumb]
          (write-scad
           (use "key-place.scad")
           (use "eggcrate.scad")
           (mirror [1 0 0]
                   (union
                    dactyl-top-right-thumb
                    (sides-connectors-thumb-from-notation sides-frame-joints)))))

(doseq [[partno part1 part2]
        (map vector (range)
             (dactyl-top-right-pieces key-holes-pieces)
             (sides-connectors-frame-from-notation sides-frame-joints))]
  (do
    (say-spit [:piece :frame :right partno]
            (write-scad
             (use "key-place.scad")
             (use "eggcrate.scad")
             (union part1 part2)))
    (say-spit [:piece :frame :left partno]
            (write-scad
             (use "key-place.scad")
             (use "eggcrate.scad")
             (mirror [1 0 0]
                     (union part1 part2))))))

(say-spit [:debugmodel :frame :right :all]
          (write-scad
           (use "key-place.scad")
           (union dactyl-top-right-thumb
                  (apply union (dactyl-top-right-pieces key-holes-pieces))
                  caps
                  thumbcaps)))

(say-spit [:debugmodel :frame :left :all]
          (write-scad
           (use "key-place.scad")
           (mirror [1 0 0]
                   (union dactyl-top-right-thumb
                          (apply union (dactyl-top-right-pieces key-holes-pieces))
                          caps
                          thumbcaps))))

(def sides-slices-right
  (pieces-with-x-pins-and-holes-faster (* sides-radius 3/4)
                                the-sides-slice-joints
                                sides-slice-intersects
                                (call-module "SidesWithRightPorts")
                                sides-regions))

(def sides-slices-left
  (pieces-with-x-pins-and-holes-faster (* sides-radius 3/4)
                                the-sides-slice-joints
                                sides-slice-intersects
                                (call-module "SidesWithLeftPorts")
                                sides-regions))

(doseq [[partno part1 part2] (map vector (range)
                                  sides-slices-right
                                  (sides-connectors-sides-from-notation
                                   sides-frame-joints
                                   sides-slices-right))]
  (say-spit [:piece :sides :right partno]
            (write-scad
             (use "key-place.scad")
             (use "eggcrate.scad")
             define-sides-with-right-ports
             (union part1 part2
                    ;; for reference when adjusting sides downness:
                    #_(union dactyl-top-right-thumb
                           (apply union (dactyl-top-right-pieces
                                         key-holes-pieces)))))))

(doseq [[partno part1 part2] (map vector (range)
                                  sides-slices-left
                                  (sides-connectors-sides-from-notation
                                   sides-frame-joints
                                   sides-slices-right))]
  (say-spit [:piece :sides :left partno]
            (write-scad
             (use "key-place.scad")
             (use "eggcrate.scad")
             define-sides-with-left-ports
             (mirror [1 0 0] (union part1 part2)))))

(say-spit [:debugmodel :splits]
          (write-scad
           (use "key-place.scad")
           (union
            (union dactyl-top-right-thumb
                   (apply union (dactyl-top-right-pieces key-holes-pieces)))
            sides-slice-intersects
            )))

(say-spit [:debugmodel :joins]
          (write-scad
           (use "key-place.scad")
           define-sides-with-right-ports
           (union
            (union dactyl-top-right-thumb
                   (apply union (dactyl-top-right-pieces key-holes-pieces)))
            (map #(% (rotate (* 1/4 τ) [0 1 0] (cylinder [10 0] 10))) the-sides-slice-joints))))

(say-spit [:debugmodel :keys]
          (write-scad
           (use "key-place.scad")
           (union
            (union caps thumbcaps))))

(say-spit [:debugmodel :photo]
          (write-scad
           (use "key-place.scad")
           (use "eggcrate.scad")
           define-sides-with-right-ports
           (union
            sides-right
            bottom-right
            (union caps thumbcaps)
            (union dactyl-top-right-thumb
                   (apply union (dactyl-top-right-pieces key-holes-pieces)))
            )))

(say-spit [:debugmodel :teensy-holder-clearance-check]
          (write-scad
           (use "key-place.scad")
           (use "eggcrate.scad")
           (use "teensy-holder.scad")
           (union
            (->> (call-module "teensy_holder_piece_a")
                 (rotate (* 1/2 τ) [0 0 1])
                 (translate [0 0 (- teensy-screw-hole-height)])
                 ((key-place-fn teensy-bracket-at)))
            bottom-right)))

(say-spit [:debugmodel :bottom :right]
          (write-scad
           (use "key-place.scad")
           (use "eggcrate.scad")
           (union
            bottom-right)
            #_(union dactyl-top-right-thumb
                   (apply union
                          (dactyl-top-right-pieces key-holes-pieces)))))

(say-spit [:debugmodel :bottom :left]
          (write-scad
           (use "key-place.scad")
           (use "eggcrate.scad")
           (mirror [1 0 0]
                   (union
                    bottom-right))))

(say-spit [:debugmodel :legs :right]
          (write-scad
           (use "key-place.scad")
           (use "eggcrate.scad")
           (legs true)
           (apply union (legs false))
           ))

(doseq [[partno leg] (map vector (range) (legs false))]
  (say-spit [:piece :legs :right partno]
            (write-scad
             (use "key-place.scad")
             (use "eggcrate.scad")
             leg)))

(doseq [[partno leg] (map vector (range) (legs false))]
  (say-spit [:piece :legs :left partno]
            (write-scad
             (use "key-place.scad")
             (use "eggcrate.scad")
             (mirror [1 0 0] leg))))

(def entire-x 180)
(def entire-y 180)
(def entire-z 120)
; set so that there aren't any little bits in the first slice
(def bottom-slice-offset 18)
(def bottom-slice-spacing (* mount-width 2.4))
;; (def bottom-slice-spacing (* mount-width 4.8))
(def bottom-glue-tolerance 0.2)
(def bottom-string-hole-frequency 1/12) ; mm^-1
(def bottom-eggcrate-resolution 3.5) ; mm
(def bottom-eggcrate-freq-y 1/30) ; mm^-1
(def bottom-eggcrate-freq-z 1/25) ; mm^-1
(def bottom-eggcrate-amplitude 15) ; mm
(doseq [slice (range (/ entire-x bottom-slice-spacing))]
  (let [slice-shape (call-module "x_space_filling_eggcrate_box"
                                 slice
                                 entire-x
                                 bottom-glue-tolerance
                                 bottom-string-hole-frequency
                                 [bottom-slice-spacing entire-y entire-z]
                                 [bottom-eggcrate-resolution
                                  bottom-eggcrate-resolution
                                  bottom-eggcrate-resolution]
                                 [1 bottom-eggcrate-freq-y
                                  bottom-eggcrate-freq-z]
                                 bottom-eggcrate-amplitude)
          placed (->> slice-shape
                      (rotate (* 3/100 τ) [0 0 1])
                      (translate [bottom-slice-offset
                                  (* -1/2 entire-y) 0])
                      (intersection bottom-right))]
      (do
        (say-spit [:piece :bottom :right slice]
                  (write-scad
                   (use "key-place.scad")
                   (use "eggcrate.scad")
                   (render placed)))
        (say-spit [:piece :bottom :left slice]
                  (write-scad
                   (use "key-place.scad")
                   (use "eggcrate.scad")
                   (mirror [1 0 0]
                           (render placed)))))))

(say-spit [:piece :screw-hole-top]
          (write-scad
           (use "eggcrate.scad")
           (screw-hole-pillar-upper screw-hole-pillar-height)))
