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
  (:require [scad-clj.scad :refer [write-scad]]
            [scad-clj.model :as m]
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

(def ^:dynamic *scad-being-written* "(unknown)")

(defn basename [filename] (last (clojure.string/split filename #"/")))

(defn ensure-line-in-file [line filename]
  (if (.exists (clojure.java.io/file filename))
    (let [lines (clojure.string/split-lines (slurp filename))]
      (if (not (some #{(clojure.string/trim line)} lines))
        (spit filename line :append true)))
    (spit filename line)))

(defn use [library]
  (do
   (ensure-line-in-file (format "%s: %s\n" (basename *scad-being-written*) library)
                        "things/.deps")
   (m/use library)))

(defn import [file & args]
  (let [fb (basename file)] ; the import statement is going to import
                            ; an stl in the things directory, and it
                            ; is being written in an scad in the
                            ; things directory
    (do
      (ensure-line-in-file (format "%s: %s\n" (basename *scad-being-written*) fb)
                           "things/.deps")
      (apply m/import (cons fb args)))))


(def thumb
  (m/union
   thumb-connectors
   (thumb-layout (m/rotate (/ π 2) [0 0 1] chosen-single-plate))
   (thumb-place 0 -1/2 double-plates)
   (thumb-place 1 -1/2 double-plates)))

(def thumb-blanks
  (m/union
   (thumb-layout (m/rotate (/ π 2) [0 0 1] chosen-blank-single-plate))
   (thumb-place 0 -1/2 double-plates-blank)
   (thumb-place 1 -1/2 double-plates-blank)))




;;;;;;;;;;;;;;;;;;;;;;;;;
;; Glue Joints for top ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(def glue-post (->> (m/cube post-size post-size glue-joint-height)
                   (m/translate [0 0 (+ (/ glue-joint-height -2)
                                      plate-thickness)])))
(def glue-post-t  (m/translate [0 (- (/ mount-height 2) post-adj) 0] glue-post))
(def glue-post-b  (m/translate [0 (+ (/ mount-height -2) post-adj) 0] glue-post))

(defn fingers-to-thumb-glue-joints-for-columns [columns]
  (apply m/union
         (for [[column row] [ [-1 3] [1 4] ] :when (some (partial = column) columns)]
           (m/union
            (key-place (- column 1/2) row
                       (m/color [1 0 0] glue-joint-center-left))
            (m/color [1 0 1]
                   (m/hull (key-place column row web-post-tl)
                         (key-place column row web-post-bl)
                         (key-place (- column 1/2) row
                                    (m/translate [(* 1/2 glue-joint-wall-thickness) 0 0]
                                               (x-round-cube (* 1/2 glue-joint-wall-thickness)
                                                             mount-height glue-joint-height)))))))))

                                        ; thumb-glue-joints doesn't
                                        ; get the same loopy
                                        ; treatment, because the 2x1
                                        ; key is different
(def thumb-to-fingers-glue-joints
  (m/union

   (key-place 1/2 4 (m/color [0 1 0] glue-joint-center-right))
   (m/color [1 0 1] (m/hull (thumb-place 0 -1/2 (m/translate [0 (/ mount-height 2) 0] web-post-tr))
                        (key-place 1/2 4 (m/translate [(* -3/2 glue-joint-wall-thickness) 0 0]
                                                    (x-round-cube (* 1/2 glue-joint-wall-thickness)
                                                                    mount-height glue-joint-height)))
                        (thumb-place 0 -1/2 (m/translate [0 (/ mount-height 2) 0] web-post-br))))

   (key-place -3/2 3 (m/color [0 1 0] glue-joint-center-right))
   (m/color [1 0 1] (m/hull (thumb-place 2 1 web-post-tr)
                        (thumb-place 2 1 web-post-br)
                        (key-place -3/2 3 (m/translate [(* -3/2 glue-joint-wall-thickness) 0 0]
                                                     (x-round-cube (* 1/2 glue-joint-wall-thickness)
                                                                   mount-height glue-joint-height)))))
   ;; this below is a block so you can clamp this joint when you glue it.
   (m/color [0 0 1] (->> (x-round-cube (* 6 glue-joint-wall-thickness)
                                     mount-height glue-joint-height)
                       (m/translate [(* -4 glue-joint-wall-thickness) 0 0])
                       (key-place -3/2 3)))))

(def right-glue-joints-for-fingerpieces
  (let [rgj-for-this
        (fn [leftmostp rightmostp columns]
          (if (not rightmostp)
            (let [column (last columns)
                  joint-column (+ column 1/2)
                  other-way (- column 1/2)]
              (apply m/union
                     (for [row rows :when (not (or (and (= column 0) (= row 4))
                                                   (and (= column -1) (= row 4))))]
                       (m/union
                                        ; the actual paddle
                        (key-place joint-column row
                                   (m/color [0 1 0] glue-joint-center-right))
                                        ; connect paddle to key-place
                        (m/color [1 0 1]
                               (m/hull
                                (key-place column row web-post-tr)
                                (key-place column row (m/translate [(- (* cherry-bezel-width 1/2)) 0 0] web-post-tr))
                                (key-place joint-column row
                                           (m/translate [(* -3/2 glue-joint-wall-thickness) 0 0]
                                                      (x-round-cube (* 1/2 glue-joint-wall-thickness)
                                                                    mount-height glue-joint-height)))
                                (key-place column row web-post-br)
                                (key-place column row (m/translate [(- (* cherry-bezel-width 1/2)) 0 0] web-post-br))))))))))]
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
            (fn [post] (m/translate [1.3 0 0] post))
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
              (apply m/union
                     (for [row rows :when (not (or (and (= column 1) (= row 4))
                                                   (and (= column 0) (= row 4))))]
                       (m/union
                                        ; the actual paddle
                        (key-place joint-column row
                                   (m/color [1 0 0] glue-joint-center-left))
                                        ; connect paddle to key-place
                        (m/color [1 0 1]
                               (m/hull
                                (key-place joint-column row
                                           (m/translate [(* 1/2 glue-joint-wall-thickness) 0 0]
                                                      (x-round-cube (* 1/2 glue-joint-wall-thickness)
                                                                    mount-height glue-joint-height)))
                                (key-place column row web-post-tl)
                                (key-place column row (m/translate [(* cherry-bezel-width 1/2) 0 0] web-post-tl))
                                (key-place column row web-post-bl)
                                (key-place column row (m/translate [(* cherry-bezel-width 1/2) 0 0] web-post-bl))))))))))]
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
      (apply m/union pieces-of-this-piece))))

(defn dactyl-top-right-minuses [key-pieces]
  (let [pieces-of-pieces (map vector
                              screw-holes-in-fingerpieces)]
    (for [pieces-of-this-piece pieces-of-pieces]
      (apply m/union pieces-of-this-piece))))

(defn dactyl-top-right-pieces [key-pieces]
  (for [[plus minus] (map vector
                          (dactyl-top-right-plusses key-pieces)
                          (dactyl-top-right-minuses key-pieces))]
        (m/difference plus minus)))

(def dactyl-top-right-thumb
  (m/difference
   (m/union thumb thumb-to-fingers-glue-joints)
   screw-holes-in-thumb))

(def define-sides-with-right-ports
  (m/define-module "SidesWithRightPorts"
    (m/with-fn 12
      (m/difference
       (m/union sides-right
              usb-nice-plate
              rj11-nice-plate)
       (usb-cutout-place adafruit-usb-cutout)
       (rj11-cutout-place rj11-cutout)))))

(def define-sides-with-left-ports
  (m/define-module "SidesWithLeftPorts"
    (m/with-fn 12
      (m/difference
       (m/union sides-right
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

(defn make-filename [tags & {:keys [ext] :or {ext ".scad"}}]
  (let [tag-abbrevs {:debugmodel "debug-"
                     :intermediate "i-"
                     :piece "dm-"
                     :frame "fra"
                     :bottom "bot"
                     :sides "sid"
                     :legs "leg"
                     :right "r-"
                     :left "l-"
                     :thumb "th"}
        stringify (fn [x] (cond
                            (integer? x) (format "%02d" x)
                            (keyword? x) (or (tag-abbrevs x) (name x))))
        filename (format "things/%s%s"
                         (clojure.string/join (map stringify tags))
                         ext)]
    filename))

(defmacro say-spit [tags & body]
  (if (emit? tags)
    `(let [filename# (make-filename ~tags)]
       (do
         (binding [*scad-being-written* filename#]
           (print (format "%s  emitting  %s\n" ~tags filename#))
           (spit filename# ~@body))))
    `(print (format "%s *SKIPPING* %s\n" ~tags (make-filename ~tags)))))

(say-spit [:debugmodel :single-plate]
      (write-scad chosen-single-plate))

(say-spit [:piece :right :frame :thumb]
          (write-scad
           (use "key-place.scad")
           (use "eggcrate.scad")
           dactyl-top-right-thumb
           (sides-connectors-thumb-from-notation sides-frame-joints)))

(say-spit [:piece :left :frame :thumb]
          (write-scad
           (use "key-place.scad")
           (use "eggcrate.scad")
           (m/mirror [1 0 0]
                   (m/union
                    dactyl-top-right-thumb
                    (sides-connectors-thumb-from-notation sides-frame-joints)))))

(doseq [[partno part1 part2]
        (map vector (range)
             (dactyl-top-right-pieces key-holes-pieces)
             (sides-connectors-frame-from-notation sides-frame-joints))]
  (do
    (say-spit [:piece :right :frame partno]
            (write-scad
             (use "key-place.scad")
             (use "eggcrate.scad")
             (m/union part1 part2)))
    (say-spit [:piece :left :frame partno]
            (write-scad
             (use "key-place.scad")
             (use "eggcrate.scad")
             (m/mirror [1 0 0]
                     (m/union part1 part2))))))

(say-spit [:debugmodel :right :frame :all]
          (write-scad
           (use "key-place.scad")
           (m/union dactyl-top-right-thumb
                  (apply m/union (dactyl-top-right-pieces key-holes-pieces))
                  caps
                  thumbcaps)))

(say-spit [:debugmodel :left :frame :all]
          (write-scad
           (use "key-place.scad")
           (m/mirror [1 0 0]
                   (m/union dactyl-top-right-thumb
                          (apply m/union (dactyl-top-right-pieces key-holes-pieces))
                          caps
                          thumbcaps))))

(def sides-slices-right
  (pieces-with-x-pins-and-holes-faster (* sides-radius 3/4)
                                the-sides-slice-joints
                                sides-slice-intersects
                                (m/call-module "SidesWithRightPorts")
                                sides-regions))

(def sides-slices-left
  (pieces-with-x-pins-and-holes-faster (* sides-radius 3/4)
                                the-sides-slice-joints
                                sides-slice-intersects
                                (m/call-module "SidesWithLeftPorts")
                                sides-regions))

(doseq [[partno part1 part2] (map vector (range)
                                  sides-slices-right
                                  (sides-connectors-sides-from-notation
                                   sides-frame-joints
                                   sides-slices-right))]
  (say-spit [:piece :right :sides partno]
            (write-scad
             (use "key-place.scad")
             (use "eggcrate.scad")
             define-sides-with-right-ports
             (m/union part1 part2
                    ;; for reference when adjusting sides downness:
                    #_(m/union dactyl-top-right-thumb
                           (apply m/union (dactyl-top-right-pieces
                                         key-holes-pieces)))))))

(doseq [[partno part1 part2] (map vector (range)
                                  sides-slices-left
                                  (sides-connectors-sides-from-notation
                                   sides-frame-joints
                                   sides-slices-right))]
  (say-spit [:piece :left :sides partno]
            (write-scad
             (use "key-place.scad")
             (use "eggcrate.scad")
             define-sides-with-left-ports
             (m/mirror [1 0 0] (m/union part1 part2)))))

(say-spit [:intermediate :right :bottom :all]
          (write-scad
           (use "key-place.scad")
           (use "eggcrate.scad")
           (m/union
            bottom-right)
            #_(m/union dactyl-top-right-thumb
                   (apply m/union
                          (dactyl-top-right-pieces key-holes-pieces)))))

(defn import-bottom-right []
  (let [bottom-right-stl-filename
        (make-filename [:intermediate :right :bottom :all] :ext ".stl")]
    (import bottom-right-stl-filename)))

(say-spit [:intermediate :left :bottom :all]
          (write-scad
           (use "key-place.scad")
           (use "eggcrate.scad")
           (m/mirror [1 0 0]
                   (m/union
                    (import-bottom-right)))))

(say-spit [:debugmodel :splits]
          (write-scad
           (use "key-place.scad")
           (m/union
            (m/union dactyl-top-right-thumb
                   (apply m/union (dactyl-top-right-pieces key-holes-pieces)))
            sides-slice-intersects
            )))

(say-spit [:debugmodel :joins]
          (write-scad
           (use "key-place.scad")
           define-sides-with-right-ports
           (m/union
            (m/union dactyl-top-right-thumb
                   (apply m/union (dactyl-top-right-pieces key-holes-pieces)))
            (map #(% (m/rotate (* 1/4 τ) [0 1 0] (m/cylinder [10 0] 10))) the-sides-slice-joints))))

(say-spit [:debugmodel :right :keys]
          (write-scad
           (use "key-place.scad")
           (m/union
            (m/union caps thumbcaps))))

(say-spit [:debugmodel :left :keys]
          (write-scad
           (use "key-place.scad")
           (m/mirror [1 0 0] (m/union caps thumbcaps))))

(say-spit [:debugmodel :photo]
          (write-scad
           (use "key-place.scad")
           (use "eggcrate.scad")
           define-sides-with-right-ports
           (m/union
            sides-right
            (import-bottom-right)
            (m/union caps thumbcaps)
            (m/union dactyl-top-right-thumb
                   (apply m/union (dactyl-top-right-pieces key-holes-pieces)))
            )))

(say-spit [:debugmodel :teensy-holder-clearance-check]
          (write-scad
           (use "key-place.scad")
           (use "eggcrate.scad")
           (use "teensy-holder.scad")
           (m/union
            (->> (m/call-module "teensy_holder_piece_a")
                 (m/rotate (* 1/2 τ) [0 0 1])
                 (m/translate [0 0 (- teensy-screw-hole-height)])
                 ((key-place-fn teensy-bracket-at)))
            (import-bottom-right))))

(say-spit [:debugmodel :right :legs :all]
          (write-scad
           (use "key-place.scad")
           (use "eggcrate.scad")
           (legs true)
           (apply m/union (legs false))
           ))

(doseq [[partno leg] (map vector (range) (legs false))]
  (say-spit [:piece :right :legs partno]
            (write-scad
             (use "key-place.scad")
             (use "eggcrate.scad")
             leg)))

(doseq [[partno leg] (map vector (range) (legs false))]
  (say-spit [:piece :left :legs partno]
            (write-scad
             (use "key-place.scad")
             (use "eggcrate.scad")
             (m/mirror [1 0 0] leg))))

(def entire-x 180)
(def entire-y 160)
(def entire-z 120)
(def bottom-egghex-minor-radius 30)
;; you may have to change these when you change the minor radius
(def bottom-lace-holes-per-side 6)
(def bottom-eggcrate-waves-per-side [0 6 28])

; set so that there aren't any little bits.
(defn offset-bottom-slices [shape]
  (m/translate [-5 8 0] shape))

(def bottom-glue-tolerance 0.1)
(def bottom-eggcrate-resolution 2) ; mm
(def bottom-eggcrate-amplitude 4) ; mm
(def bottom-lace-hole-r 1.0) ; mm
(def bottom-lace-hole-in-from-edge 2.2) ; mm

(defn with-egghex-splitters [use-splitter prepended-tags]
  (let [rmin bottom-egghex-minor-radius
        side (* rmin 2 (Math/tan (/ (* 1/2 τ) 6)))
        ;; 6 : hexagon
        rmaj (/ rmin (Math/cos (/ (* 1/2 τ) 6)))
        egghex-columns (Math/ceil (/ (+ entire-x rmin)
                                (+ (* 2 bottom-egghex-minor-radius)
                                   bottom-glue-tolerance)))
        egghex-row-spacing (+ (* 3/2 side) (* bottom-glue-tolerance
                                              (Math/sin (/ τ 6))))
        egghex-rows (Math/ceil (/ entire-y
                             (+ egghex-row-spacing
                                bottom-glue-tolerance)))]
  (doseq [slice (range (* egghex-columns egghex-rows))]
    (let [slice-shape (m/call-module "hex_prism_of_grid"
                                   [entire-x entire-y entire-z]
                                   slice
                                   rmin
                                   bottom-glue-tolerance
                                   [bottom-eggcrate-resolution
                                    bottom-eggcrate-resolution
                                    bottom-eggcrate-resolution]
                                   bottom-eggcrate-waves-per-side
                                   [bottom-eggcrate-amplitude
                                    bottom-eggcrate-amplitude
                                    bottom-eggcrate-amplitude]
                                   1 bottom-lace-holes-per-side
                                   bottom-lace-hole-r
                                   bottom-lace-hole-in-from-edge
                                   )
          place (fn [shape] (->> shape
                                 (m/rotate (* 2/100 τ) [0 0 1])
                                 (offset-bottom-slices)
                                 (m/translate [(* -1/2 entire-x)
                                             (* -1/2 entire-y) 0])))
          axes (m/union
                (m/cube entire-x 10 10)
                (m/translate [0 (* 1/2 entire-y) 0]
                           (m/cube 10 entire-y 10))
                (m/translate [0 0 (* 1/2 entire-z)]
                             (m/cube 10 10 entire-z)))
          ; make this a lambda so when use-splitter is evaluated we
          ; will be inside a (say-spit) and *scad-being-written* will
          ; be defined
          of-interest (fn [] (use-splitter (place slice-shape)))]
      (do
        (say-spit (apply vector (concat [(first prepended-tags)]
                                        [:right]
                                        (rest prepended-tags)
                                        [slice]))
                  (write-scad
                   (use "key-place.scad")
                   (use "eggcrate.scad")
                   (use "egghex2.scad")
                   (m/union
                    (m/render (of-interest))
                    #_(place axes))))
        (say-spit (apply vector (concat [(first prepended-tags)]
                                        [:left]
                                        (rest prepended-tags)
                                        [slice]))
                  (write-scad
                   (use "key-place.scad")
                   (use "eggcrate.scad")
                   (use "egghex2.scad")
                   (m/mirror [1 0 0]
                           (m/union
                            (m/render (of-interest))
                            #_(place axes))))))))))

(with-egghex-splitters
  #(m/intersection (import-bottom-right) %)
  [:piece :bottom])

(let [all-frame (m/union
                 dactyl-top-right-thumb
                 (apply m/union (dactyl-top-right-pieces key-holes-pieces)))]
  (with-egghex-splitters
    #(m/union all-frame %)
    [:debugmodel :splitter-frame]))

(say-spit [:piece :screw-hole-top]
          (write-scad
           (use "eggcrate.scad")
           (screw-hole-pillar-upper screw-hole-pillar-height)))
