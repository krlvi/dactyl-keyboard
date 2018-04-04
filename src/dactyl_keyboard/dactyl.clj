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
            [dactyl-keyboard.marshmallowy-sides :refer :all]
            [dactyl-keyboard.marshmallowy-sides-pieces :refer :all]
            [dactyl-keyboard.teensy :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.half-circle-connectors :refer :all]
            [dactyl-keyboard.adafruit-usb :refer :all]))




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




                                        ; https://www.mouser.com/ds/2/18/61835-1003706.pdf
(def rj11-face-width 11.18)
(def rj11-face-height 15.87)
(def rj11-body-width 13.72)


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
                        (thumb-place 2 1 web-post-br) (key-place -3/2 3 (translate [(- 0 (* 3/2 glue-joint-wall-thickness)) 0 0] glue-post-b))))))

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

#_(def dactyl-bottom-right
  (difference
   (union
    teensy-cover
    (difference
     bottom-plate
     (hull teensy-cover)
     new-case-fingers
     new-case-thumb
     teensy-cover
     trrs-cutout
     (->> (cube 1000 1000 10) (translate [0 0 -5]))
     screw-holes))
   usb-cutout))

#_(def dactyl-bottom-left
  (mirror [-1 0 0]
          (union
           io-exp-cover
           (difference
            bottom-plate
            (hull io-exp-cover)
            new-case-fingers
            new-case-thumb
            io-exp-cover
            trrs-cutout
            (->> (cube 1000 1000 10) (translate [0 0 -5]))
            screw-holes))))

(defn dactyl-top-right-pieces [key-pieces]
  ; agh i made bad names and now i pay for it
  (let [pieces-of-pieces (map vector
                              (map fingers-to-thumb-glue-joints-for-columns columns-pieces)
                              right-glue-joints-for-fingerpieces
                              key-pieces
                              connectors-inside-fingerpieces
                              left-glue-joints-for-fingerpieces
                              (repeat teensy-support))]
    (for [pieces-of-this-piece pieces-of-pieces]
      (apply union pieces-of-this-piece))))

#_(def dactyl-top-left
  (mirror [-1 0 0]
          (difference
           (union key-holes
                  connectors-a
                  connectors-b
                  thumb
                  new-case-fingers new-case-thumb)
           trrs-hole-just-circle
           screw-holes)))

(def dactyl-top-right-thumb
  (union thumb
         thumb-to-fingers-glue-joints))

#_(let [abbreviation-cylinder (translate [-30 -40 0] (cylinder 40 140))]
  (def dactyl-top-right-abbreviated
    (intersection dactyl-top-right abbreviation-cylinder))
  (def dactyl-top-right-thumb-abbreviated
    (intersection dactyl-top-right-thumb abbreviation-cylinder))
  (def dactyl-top-right-both-abbreviated
    (intersection (union dactyl-top-right dactyl-top-right-thumb) abbreviation-cylinder)))



;; (spit "things/switch-hole.scad"
      ;; (write-scad single-plate))

;; (spit "things/alps-holes.scad"
      ;; (write-scad (union connectors key-holes)))

(doseq [[partno part] (map vector (range) (dactyl-top-right-pieces key-holes-pieces))]
  (spit (format "things/dactyl-top-right-%02d.scad" partno)
        (write-scad part)))

(spit "things/dactyl-top-right-all.scad"
      (write-scad
       (union dactyl-top-right-thumb
              (apply union (dactyl-top-right-pieces key-holes-pieces))
              caps
              thumbcaps)))

(def mallowy-sides-with-right-ports
  (difference
   (binding [*fn* 12]
     (union
      mallowy-sides-right
      (usb-cutout-place adafruit-usb-plate)))
   (usb-cutout-place adafruit-usb-cutout)))

(def mallow-slices-right
  (pieces-with-x-pins-and-holes (* marshmallowy-sides-radius 3/4)
                                marshmallow-slice-joints
                                marshmallow-slice-intersects
                                mallowy-sides-with-right-ports))

(doseq [[partno part] (map vector (range) mallow-slices-right)]
  (spit (format "things/marshmallow-right-%02d.scad" partno)
        (write-scad (union part))))

(spit "things/splits.scad"
      (write-scad
       (union
        (union dactyl-top-right-thumb
               (apply union (dactyl-top-right-pieces key-holes-pieces)))
        marshmallow-slice-intersects
        )))

(spit "things/joins.scad"
      (write-scad
       (union
        (union dactyl-top-right-thumb
               (apply union (dactyl-top-right-pieces key-holes-pieces)))
        (map #(% (rotate (* 1/4 τ) [0 1 0] (cylinder [10 0] 10))) marshmallow-slice-joints))))


(spit "things/dactyl-blank-all.scad"
      (write-scad
       (union
        mallowy-sides-right
        #_(union
         (finger-case-bottom-sphere marshmallowy-sides-flatness marshmallowy-sides-downness)
         (thumb-case-bottom-sphere marshmallowy-sides-flatness marshmallowy-sides-downness))

        #_(union
         (apply union key-blanks-pieces)
         thumb-blanks)
        #_(union caps thumbcaps)
        #_(union
         (thumb-key-prism 30 -5)
         (finger-key-prism 30 -5))
        #_(color [0 1 0 0.7] (finger-prism 30 0))
        #_(color [0 1 0 0.7] (thumb-top-outline-prism2 45 0))
        (sides-connector-frame-e key-place 1 2 3)
        (sides-connector-frame-w thumb-place 2 1 0)
        (sides-connector-frame-n key-place -1 0 2)
        (sides-connector-frame-s thumb-place 1 0 -1)
        (sides-connector-sides-e key-place 1 2 3 mallowy-sides-right)
        (sides-connector-sides-w thumb-place 2 1 0 mallowy-sides-right)
        (sides-connector-sides-n key-place -1 0 2 mallowy-sides-right)
        (sides-connector-sides-s thumb-place 1 0 -1 mallowy-sides-right)
        (union dactyl-top-right-thumb
                 (apply union (dactyl-top-right-pieces key-holes-pieces)))
        )))

#_(spit "things/dactyl-bottom-right.scad"
      (write-scad dactyl-bottom-right))

;; (spit "things/dactyl-top-left.scad"
      ;; (write-scad dactyl-top-left))

;; (spit "things/dactyl-bottom-left.scad"
       ;; (write-scad dactyl-bottom-left))

;; (spit "things/dactyl-top-left-with-teensy.scad"
;;       (write-scad (mirror [-1 0 0] dactyl-top-right)))

;; (spit "things/dactyl-bottom-left-with-teensy.scad"
;;       (write-scad (mirror [-1 0 0] dactyl-bottom-right)))
