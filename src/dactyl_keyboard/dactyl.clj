(ns dactyl-keyboard.dactyl
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [unicode-math.core :refer :all]))

;;;;;;;;;;;;;;;;;
;; Switch Hole ;;
;;;;;;;;;;;;;;;;;

(def keyswitch-height 14.4) ;; Was 14.1, then 14.25
(def keyswitch-width 14.4)

(def sa-profile-key-height 12.7)

(def plate-thickness 4)
(def mount-width (+ keyswitch-width 3))
(def mount-height (+ keyswitch-height 3))



(def alps-width 15.6)
(def alps-notch-width 15.5)
(def alps-notch-height 1)
(def alps-height 13)

(def cherry-bezel-width 1.5)
(def cherry-single-plate
  (let [top-wall (->> (cube (+ keyswitch-width (* 2 cherry-bezel-width)) cherry-bezel-width plate-thickness)
                      (translate [0
                                  (+ (/ cherry-bezel-width 2) (/ keyswitch-height 2))
                                  (/ plate-thickness 2)]))
        left-wall (->> (cube 1.5 (+ keyswitch-height (* 2 cherry-bezel-width)) plate-thickness)
                       (translate [(+ (/ cherry-bezel-width 2) (/ keyswitch-width 2))
                                   0
                                   (/ plate-thickness 2)]))
        side-nub (->> (binding [*fn* 30] (cylinder 1 2.75))
                      (rotate (/ π 2) [1 0 0])
                      (translate [(+ (/ keyswitch-width 2)) 0 1])
                      (hull (->> (cube cherry-bezel-width 2.75 plate-thickness)
                                 (translate [(+ (/ cherry-bezel-width 2) (/ keyswitch-width 2))
                                             0
                                             (/ plate-thickness 2)]))))
        plate-half (union top-wall left-wall (with-fn 100 side-nub))]
    (union plate-half
           (->> plate-half
                (mirror [1 0 0])
                (mirror [0 1 0])))))

(def cherry-blank-single-plate
  (->>
    (cube (+ keyswitch-width (* 2 cherry-bezel-width))
          (+ keyswitch-height (* 2 cherry-bezel-width))
          plate-thickness)
    (translate [0 0 (/ plate-thickness 2)])))

(def alps-single-plate
  (let [top-wall (->> (cube (+ keyswitch-width 3) 2.2 plate-thickness)
                      (translate [0
                                  (+ (/ 2.2 2) (/ alps-height 2))
                                  (/ plate-thickness 2)]))
        left-wall (union (->> (cube 1.5 (+ keyswitch-height 3) plate-thickness)
                              (translate [(+ (/ 1.5 2) (/ 15.6 2))
                                          0
                                          (/ plate-thickness 2)]))
                         (->> (cube 1.5 (+ keyswitch-height 3) 1.0)
                              (translate [(+ (/ 1.5 2) (/ alps-notch-width 2))
                                          0
                                          (- plate-thickness
                                             (/ alps-notch-height 2))]))
                         )
        plate-half (union top-wall left-wall)]
    (union plate-half
           (->> plate-half
                (mirror [1 0 0])
                (mirror [0 1 0])))))

(def chosen-single-plate cherry-single-plate)
(def chosen-blank-single-plate cherry-blank-single-plate)

; Paddles that stick out below, so the fingers and thumb can be
; printed separately then glued together.

(def glue-joint-height (* 2 plate-thickness))
(def glue-joint-wall-thickness 1.2) ; how much plastic for the paddle
(def glue-joint-glue-thickness 0.1) ; how much thickness to leave for glue

(def glue-joint-lug
  (cube (/ glue-joint-wall-thickness 2)
        (/ mount-height 4)
        (/ glue-joint-height 4)))

(def glue-joint-not-lug
  (let [tolerance (* 2 glue-joint-glue-thickness)]
    (cube (+ (/ glue-joint-wall-thickness 2) tolerance)
          (+ (/ mount-height 4) tolerance)
          (+ (/ glue-joint-height 4) tolerance))))

(defn glue-joint-l-places [shape]
  (union
   (translate [(- 0 (/ glue-joint-wall-thickness 2))
               (- 0 (/ mount-height 4))
               (- 0 (/ glue-joint-height 4))]
              shape)
   (translate [(- 0 (/ glue-joint-wall-thickness 2))
               (/ mount-height 4)
               (/ glue-joint-height 4)]
              shape)))

(defn glue-joint-r-places [shape]
  (union
   (translate [(/ glue-joint-wall-thickness 2)
               (- 0 (/ mount-height 4))
               (- 0 (/ glue-joint-height 4))]
              shape)
   (translate [(/ glue-joint-wall-thickness 2)
               (/ mount-height 4)
               (/ glue-joint-height 4)]
              shape)))

                                        ; glue-joint-r-shape is at the
                                        ; far left of a key space, and
                                        ; goes on the right side of a
                                        ; glue joint
(def glue-joint-r-shape
  (difference
         (cube glue-joint-wall-thickness
               mount-height
               glue-joint-height)
         (glue-joint-r-places glue-joint-not-lug)))

                                        ; glue-joint-l-shape is at the
                                        ; far right of a key space,
                                        ; and goes on the left side of
                                        ; a glue joint
(def glue-joint-l-shape
  (union
         (cube glue-joint-wall-thickness
               mount-height
               glue-joint-height)
         (glue-joint-l-places glue-joint-lug)))

(def glue-joint-tb-shape (cube mount-width
                                glue-joint-wall-thickness
                                glue-joint-height))

  (def glue-joint-center-left
    (->> glue-joint-l-shape
         (translate [(/ glue-joint-glue-thickness 2)
                     0
                     (- plate-thickness (/ glue-joint-height 2))])))
  (def glue-joint-center-right
    (->> glue-joint-r-shape
         (translate [(- 0 glue-joint-wall-thickness (/ glue-joint-glue-thickness 2))
                     0
                     (- plate-thickness (/ glue-joint-height 2))])))



;;;;;;;;;;;;;;;;
;; SA Keycaps ;;
;;;;;;;;;;;;;;;;

(def sa-length 18.25)
(def sa-double-length 37.5)
(def sa-cap {1 (let [bl2 (/ 18.5 2)
                     m (/ 17 2)
                     key-cap (hull (->> (polygon [[bl2 bl2] [bl2 (- bl2)] [(- bl2) (- bl2)] [(- bl2) bl2]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 0.05]))
                                   (->> (polygon [[m m] [m (- m)] [(- m) (- m)] [(- m) m]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 6]))
                                   (->> (polygon [[6 6] [6 -6] [-6 -6] [-6 6]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 12])))]
                 (->> key-cap
                      (translate [0 0 (+ 5 plate-thickness)])
                      (color [220/255 163/255 163/255 1])))
             2 (let [bl2 (/ sa-double-length 2)
                     bw2 (/ 18.25 2)
                     key-cap (hull (->> (polygon [[bw2 bl2] [bw2 (- bl2)] [(- bw2) (- bl2)] [(- bw2) bl2]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 0.05]))
                                   (->> (polygon [[6 16] [6 -16] [-6 -16] [-6 16]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 12])))]
                 (->> key-cap
                      (translate [0 0 (+ 5 plate-thickness)])
                      (color [127/255 159/255 127/255 1])))
             1.5 (let [bl2 (/ 18.25 2)
                       bw2 (/ 28 2)
                       key-cap (hull (->> (polygon [[bw2 bl2] [bw2 (- bl2)] [(- bw2) (- bl2)] [(- bw2) bl2]])
                                          (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                          (translate [0 0 0.05]))
                                     (->> (polygon [[11 6] [-11 6] [-11 -6] [11 -6]])
                                          (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                          (translate [0 0 12])))]
                   (->> key-cap
                        (translate [0 0 (+ 5 plate-thickness)])
                        (color [240/255 223/255 175/255 1])))})

;;;;;;;;;;;;;;;;;;;;;;;;;
;; Placement Functions ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

; we split the keyholes into pieces for smaller print volume and quicker printing
(def columns-pieces [(range -1 0) (range 0 2) (range 2 4) (range 4 6)])
(def columns   (apply concat columns-pieces))
(def rows (range 0 5))
(def tenting-angle (/ π 12))

(def α (/ π 12))
(def β (/ π 36))
(def cap-top-height (+ plate-thickness sa-profile-key-height))
(def row-radius (+ (/ (/ (+ mount-height 1/2) 2)
                      (Math/sin (/ α 2)))
                   cap-top-height))
(def column-radius (+ (/ (/ (+ mount-width 2.0) 2)
                         (Math/sin (/ β 2)))
                      cap-top-height))

(defn key-place [column row shape]
  (let [row-placed-shape (->> shape
                              (translate [0 0 (- row-radius)])
                              (rotate (* α (- 2 row)) [1 0 0])
                              (translate [0 0 row-radius]))
        column-offset (cond
                        (and (>= column 2) (< column 3)) [0 2.82 -3.0] ;;was moved -4.5
                        (>= column 4) [0 -5.8 5.64]
                        :else [0 0 0])
        column-angle (* β (- 2 column))
        placed-shape (->> row-placed-shape
                          (translate [0 0 (- column-radius)])
                          (rotate column-angle [0 1 0])
                          (translate [0 0 column-radius])
                          (translate column-offset))]
    (->> placed-shape
         (rotate tenting-angle [0 1 0])
         (translate [0 0 13]))))


(defn case-place [column row shape]
  (let [row-placed-shape (->> shape
                              (translate [0 0 (- row-radius)])
                              (rotate (* α (- 2 row)) [1 0 0])
                              (translate [0 0 row-radius]))
        column-offset [0 -4.35 5.64]
        column-angle (* β (- 2 column))
        placed-shape (->> row-placed-shape
                          (translate [0 0 (- column-radius)])
                          (rotate column-angle [0 1 0])
                          (translate [0 0 column-radius])
                          (translate column-offset))]
    (->> placed-shape
         (rotate tenting-angle [0 1 0])
         (translate [0 0 13]))))

(defn finger-has-key-place-p [row column]
  (cond
    (and (= column 0) (= row 4)) false
    (and (= column -1) (= row 4)) false
    true true))

(defn thumb-glue-joint-left-of-p [row column]
  (cond
    (and (= column -1) (= row 3)) true
    (and (= column 1) (= row 4)) true
    true false))

(defn key-shapes-for-columns [shape columns]
  (apply union
         (for [column columns
               row rows
               :when (finger-has-key-place-p row column)]
           (->> shape
                (key-place column row)))))

(def key-holes-pieces (map #(key-shapes-for-columns chosen-single-plate %) columns-pieces))
(def key-holes   (key-shapes-for-columns chosen-single-plate columns))

(def key-blanks-pieces (map #(key-shapes-for-columns chosen-blank-single-plate %) columns-pieces))
(def key-blanks (key-shapes-for-columns chosen-blank-single-plate columns))


(def caps
  (apply union
         (for [column columns
               row rows
               :when (finger-has-key-place-p row column)]
           (->> (sa-cap (if (= column 5) 1 1))
                (key-place column row)))))

;;;;;;;;;;;;;;;;;;;;
;; Web Connectors ;;
;;;;;;;;;;;;;;;;;;;;

(def web-thickness 3.5)
(def post-size 0.1)
(def web-post (->> (cube post-size post-size web-thickness)
                   (translate [0 0 (+ (/ web-thickness -2)
                                      plate-thickness)])))

(def post-adj (/ post-size 2))
(def web-post-tr (translate [(- (/ mount-width 2) post-adj) (- (/ mount-height 2) post-adj) 0] web-post))
(def web-post-tl (translate [(+ (/ mount-width -2) post-adj) (- (/ mount-height 2) post-adj) 0] web-post))
(def web-post-t  (translate [0 (- (/ mount-height 2) post-adj) 0] web-post))

(def web-post-bl (translate [(+ (/ mount-width -2) post-adj) (+ (/ mount-height -2) post-adj) 0] web-post))
(def web-post-br (translate [(- (/ mount-width 2) post-adj) (+ (/ mount-height -2) post-adj) 0] web-post))
(def web-post-b  (translate [0 (+ (/ mount-height -2) post-adj) 0] web-post))

(defn row-connectors [column]
  (for [row rows
        :when (finger-has-key-place-p row column)]
    (triangle-hulls
     (key-place (inc column) row web-post-tl)
     (key-place column row web-post-tr)
     (key-place (inc column) row web-post-bl)
     (key-place column row web-post-br))))

(defn diagonal-connectors [column]
  (for [row (drop-last rows)
        :when (and
               (finger-has-key-place-p (inc row) (inc column))
               (not (thumb-glue-joint-left-of-p (inc row) (inc column))))]
    (triangle-hulls
     (key-place column row web-post-br)
     (key-place column (inc row) web-post-tr)
     (key-place (inc column) row web-post-bl)
     (key-place (inc column) (inc row) web-post-tl))))

(defn column-connectors [column]
  (for [row (drop-last rows)
        :when (finger-has-key-place-p (inc row) column)]
    (triangle-hulls
     (key-place column row web-post-bl)
     (key-place column row web-post-br)
     (key-place column (inc row) web-post-tl)
     (key-place column (inc row) web-post-tr))))

(def connectors-inside-fingerpieces
  (let
      [connectors-for-columns
       (fn [columns]
         (apply union
                (for [column (drop-last columns)]
                  (concat
                   (row-connectors column)
                   (diagonal-connectors column)))
                 (for [column columns]
                   (column-connectors column))))]
    (map connectors-for-columns columns-pieces)))

;;;;;;;;;;;;
;; Thumbs ;;
;;;;;;;;;;;;

(defn thumb-place [column row shape]
  (let [cap-top-height (+ plate-thickness sa-profile-key-height)
        α (/ π 12)
        row-radius (+ (/ (/ (+ mount-height 1) 2)
                         (Math/sin (/ α 2)))
                      cap-top-height)
        β (/ π 36)
        column-radius (+ (/ (/ (+ mount-width 2) 2)
                            (Math/sin (/ β 2)))
                         cap-top-height)
        #_(+ (/ (/ (+ pillar-width 5) 2)
                            (Math/sin (/ β 2)))
                         cap-top-height)]
    (->> shape
         (translate [0 0 (- row-radius)])
         (rotate (* α row) [1 0 0])
         (translate [0 0 row-radius])
         (translate [0 0 (- column-radius)])
         (rotate (* column β) [0 1 0])
         (translate [0 0 column-radius])
         (translate [mount-width 0 0])
         (rotate (* π (- 1/4 3/16)) [0 0 1])
         ; perhaps this should be tenting-angle, but look at the
         ; axis. that will influence what the translation after it
         ; needs to be
         (rotate (/ π 12) [1 1 0])
         (translate [-53 -45 40]))))

(defn thumb-2x-column [shape]
  (thumb-place 0 -1/2 shape))

(defn thumb-2x+1-column [shape]
  (thumb-place 1 -1/2 shape))

(defn thumb-1x-column [shape]
  (union (thumb-place 2 -1 shape)
         (thumb-place 2 0 shape)
         (thumb-place 2 1 shape)))

(defn thumb-layout [shape]
  (union
   (thumb-2x-column shape)
   (thumb-2x+1-column shape)
   (thumb-1x-column shape)))

(def double-plates
  (let [plate-height (/ (- sa-double-length mount-height) 2)
        top-plate (->> (cube mount-width plate-height web-thickness)
                       (translate [0 (/ (+ plate-height mount-height) 2)
                                   (- plate-thickness (/ web-thickness 2))]))
        stabilizer-cutout (union (->> (cube 14.2 3.5 web-thickness)
                                      (translate [0.5 12 (- plate-thickness (/ web-thickness 2))])
                                      (color [1 0 0 1/2]))
                                 (->> (cube 16 3.5 web-thickness)
                                      (translate [0.5 12 (- plate-thickness (/ web-thickness 2) 1.4)])
                                      (color [1 0 0 1/2])))
        top-plate (difference top-plate stabilizer-cutout)]
    (union top-plate (mirror [0 1 0] top-plate))))

(def thumbcaps
  (union
   (thumb-2x-column (sa-cap 2))
   (thumb-place 1 -1/2 (sa-cap 2))
   #_(thumb-place 1 1 (sa-cap 1))
   (thumb-1x-column (sa-cap 1))))

(def thumb-connectors
  (union
   (apply union
          (concat
           #_(for [column [2] row [1]]
             (triangle-hulls (thumb-place column row web-post-br)
                             (thumb-place column row web-post-tr)
                             (thumb-place (dec column) row web-post-bl)
                             (thumb-place (dec column) row web-post-tl)))
           (for [column [2] row [0 1]]
             (triangle-hulls
              (thumb-place column row web-post-bl)
              (thumb-place column row web-post-br)
              (thumb-place column (dec row) web-post-tl)
              (thumb-place column (dec row) web-post-tr)))))
   (let [plate-height (/ (- sa-double-length mount-height) 2)
         thumb-tl (->> web-post-tl
                       (translate [0 plate-height 0]))
         thumb-bl (->> web-post-bl
                       (translate [0 (- plate-height) 0]))
         thumb-tr (->> web-post-tr
                       (translate [0 plate-height 0]))
         thumb-br (->> web-post-br
                       (translate [0 (- plate-height) 0]))]
     (union

      ;;Connecting the two doubles
      (triangle-hulls (thumb-place 0 -1/2 thumb-tl)
                      (thumb-place 0 -1/2 thumb-bl)
                      (thumb-place 1 -1/2 thumb-tr)
                      (thumb-place 1 -1/2 thumb-br))

      ;;Connecting the 4 with the double in the bottom left
      #_(triangle-hulls (thumb-place 1 1 web-post-bl)
                      (thumb-place 1 -1/2 thumb-tl)
                      (thumb-place 2 1 web-post-br)
                      (thumb-place 2 0 web-post-tr))

      ;;Connecting the two singles with the middle double
      (hull (thumb-place 1 -1/2 thumb-tl)
            (thumb-place 1 -1/2 thumb-bl)
            (thumb-place 2 0 web-post-br)
            (thumb-place 2 -1 web-post-tr))
      (hull (thumb-place 1 -1/2 thumb-tl)
            (thumb-place 2 0 web-post-tr)
            (thumb-place 2 0 web-post-br))
      (hull (thumb-place 1 -1/2 thumb-bl)
            (thumb-place 2 -1 web-post-tr)
            (thumb-place 2 -1 web-post-br))

      ;;Connecting the thumb to everything
      #_(triangle-hulls (thumb-place 0 -1/2 thumb-br)
                      (key-place 1 4 web-post-bl)
                      (thumb-place 0 -1/2 thumb-tr)
                      (key-place 1 4 web-post-tl)
                      (key-place 1 3 web-post-bl)
                      (thumb-place 0 -1/2 thumb-tr)
                      (key-place 0 3 web-post-br)
                      (key-place 0 3 web-post-bl)
                      (thumb-place 0 -1/2 thumb-tr)
                      (thumb-place 0 -1/2 thumb-tl)
                      (key-place 0 3 web-post-bl)
                      (thumb-place 1 -1/2 thumb-tr)
                      (thumb-place 1 1 web-post-br)
                      (key-place 0 3 web-post-bl)
                      (key-place 0 3 web-post-tl)
                      (thumb-place 1 1 web-post-br)
                      (thumb-place 1 1 web-post-tr))
      #_(hull (thumb-place 0 -1/2 web-post-tr)
            (thumb-place 0 -1/2 thumb-tr)
            (key-place 1 4 web-post-bl)
            (key-place 1 4 web-post-tl))
      ))))

(def thumb
  (union
   thumb-connectors
   (thumb-layout (rotate (/ π 2) [0 0 1] chosen-single-plate))
   (thumb-place 0 -1/2 double-plates)
   (thumb-place 1 -1/2 double-plates)))

;;;;;;;;;;
;; Case ;;
;;;;;;;;;;

;; In column units
(def right-wall-column (+ (last columns) 0.55))
(def left-wall-column (- (first columns) 1/2))
(def thumb-back-y 0.93)
(def thumb-right-wall (- -1/2 0.05))
(def thumb-front-row (+ -1 0.07))
(def thumb-left-wall-column (+ 5/2 0.05))
(def back-y 0.02)

(defn range-inclusive [start end step]
  (concat (range start end step) [end]))

(def wall-step 0.2)
(def wall-sphere-n 20) ;;Sphere resolution, lower for faster renders

(defn wall-sphere-at [coords]
  (->> (sphere 1)
       (translate coords)
       (with-fn wall-sphere-n)))

(defn scale-to-range [start end x]
  (+ start (* (- end start) x)))

(defn wall-sphere-bottom [front-to-back-scale]
  (wall-sphere-at [0
                   (scale-to-range
                    (+ (/ mount-height -2) -3.5)
                    (+ (/ mount-height 2) 5.0)
                    front-to-back-scale)
                   -6]))

(defn wall-sphere-top [front-to-back-scale]
  (wall-sphere-at [0
                   (scale-to-range
                    (+ (/ mount-height -2) -3.5)
                    (+ (/ mount-height 2) 3.5)
                    front-to-back-scale)
                   10]))

(def wall-sphere-top-back (wall-sphere-top 1))
(def wall-sphere-bottom-back (wall-sphere-bottom 1))
(def wall-sphere-bottom-front (wall-sphere-bottom 0))
(def wall-sphere-top-front (wall-sphere-top 0))

(defn top-case-cover [place-fn sphere
                 x-start x-end
                 y-start y-end
                 step]
  (apply union
         (for [x (range-inclusive x-start (- x-end step) step)
               y (range-inclusive y-start (- y-end step) step)]
           (hull (place-fn x y sphere)
                 (place-fn (+ x step) y sphere)
                 (place-fn x (+ y step) sphere)
                 (place-fn (+ x step) (+ y step) sphere)))))

(def front-wall
  (let [step wall-step ;;0.1
        wall-step 0.05 ;;0.05
        place case-place
        top-cover (fn [x-start x-end y-start y-end]
                    (top-case-cover place wall-sphere-top-front
                                    x-start x-end y-start y-end
                                    wall-step))]
    (union
     (apply union
            (for [x (range-inclusive 0.7 (- right-wall-column step) step)]
              (hull (place x 4 wall-sphere-top-front)
                    (place (+ x step) 4 wall-sphere-top-front)
                    (place x 4 wall-sphere-bottom-front)
                    (place (+ x step) 4 wall-sphere-bottom-front))))
     (apply union
            (for [x (range-inclusive 0.5 0.7 0.01)]
              (hull (place x 4 wall-sphere-top-front)
                    (place (+ x step) 4 wall-sphere-top-front)
                    (place 0.7 4 wall-sphere-bottom-front))))
     (top-cover 0.5 1.7 3.6 4)
     (top-cover 1.59 2.41 3.35 4) ;; was 3.32
     (top-cover 2.39 3.41 3.6 4)
     (apply union
            (for [x (range 2 5)]
              (union
               (hull (place (- x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                     (place (+ x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                     (key-place x 4 web-post-bl)
                     (key-place x 4 web-post-br))
               (hull (place (- x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                     (key-place x 4 web-post-bl)
                     (key-place (- x 1) 4 web-post-br)))))
     (hull (place right-wall-column 4 (translate [0 1 1] wall-sphere-bottom-front))
           (place (- right-wall-column 1) 4 (translate [0 1 1] wall-sphere-bottom-front))
           (key-place 5 4 web-post-bl)
           (key-place 5 4 web-post-br))
     (hull (place (+ 4 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
           (place (- right-wall-column 1) 4 (translate [0 1 1] wall-sphere-bottom-front))
           (key-place 4 4 web-post-br)
           (key-place 5 4 web-post-bl))
     (hull (place 0.7 4 (translate [0 1 1] wall-sphere-bottom-front))
           (place 1.7 4 (translate [0 1 1] wall-sphere-bottom-front))
           (key-place 1 4 web-post-bl)
           (key-place 1 4 web-post-br)))))

(def back-wall
  (let [step wall-step
        wall-sphere-top-backtep 0.05
        place case-place
        front-top-cover (fn [x-start x-end y-start y-end]
                          (apply union
                                 (for [x (range-inclusive x-start (- x-end wall-sphere-top-backtep) wall-sphere-top-backtep)
                                       y (range-inclusive y-start (- y-end wall-sphere-top-backtep) wall-sphere-top-backtep)]
                                   (hull (place x y wall-sphere-top-back)
                                         (place (+ x wall-sphere-top-backtep) y wall-sphere-top-back)
                                         (place x (+ y wall-sphere-top-backtep) wall-sphere-top-back)
                                         (place (+ x wall-sphere-top-backtep) (+ y wall-sphere-top-backtep) wall-sphere-top-back)))))]
    (union
     (apply union
            (for [x (range-inclusive left-wall-column (- right-wall-column step) step)]
              (hull (place x back-y wall-sphere-top-back)
                    (place (+ x step) back-y wall-sphere-top-back)
                    (place x back-y wall-sphere-bottom-back)
                    (place (+ x step) back-y wall-sphere-bottom-back))))
     (front-top-cover 1.56 2.44 back-y 0.1)
     (front-top-cover 3.56 4.44 back-y 0.13)
     (front-top-cover 4.3 right-wall-column back-y 0.13)


     (hull (place left-wall-column 0 (translate [1 -1 1] wall-sphere-bottom-back))
           (place (+ left-wall-column 1) 0  (translate [0 -1 1] wall-sphere-bottom-back))
           (key-place 0 0 web-post-tl)
           (key-place 0 0 web-post-tr))

     (hull (place 5 0 (translate [0 -1 1] wall-sphere-bottom-back))
           (place right-wall-column 0 (translate [0 -1 1] wall-sphere-bottom-back))
           (key-place 5 0 web-post-tl)
           (key-place 5 0 web-post-tr))

     (apply union
            (for [x (range 1 5)]
              (union
               (hull (place (- x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                     (place (+ x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                     (key-place x 0 web-post-tl)
                     (key-place x 0 web-post-tr))
               (hull (place (- x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                     (key-place x 0 web-post-tl)
                     (key-place (- x 1) 0 web-post-tr)))))
     (hull (place (- 5 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
           (place 5 0 (translate [0 -1 1] wall-sphere-bottom-back))
           (key-place 4 0 web-post-tr)
           (key-place 5 0 web-post-tl)))))

(def right-wall
  (let [place case-place]
    (union
     (apply union
            (map (partial apply hull)
                 (partition 2 1
                            (for [scale (range-inclusive 0 1 0.01)]
                              (let [x (scale-to-range 4 0.02 scale)]
                                (hull (place right-wall-column x (wall-sphere-top scale))
                                      (place right-wall-column x (wall-sphere-bottom scale))))))))

          (apply union
            (concat
             (for [x (range 0 5)]
               (union
                (hull (place right-wall-column x (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                      (key-place 5 x web-post-br)
                      (key-place 5 x web-post-tr))))
             (for [x (range 0 4)]
               (union
                (hull (place right-wall-column x (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                      (place right-wall-column (inc x) (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                      (key-place 5 x web-post-br)
                      (key-place 5 (inc x) web-post-tr))))
             [(union
               (hull (place right-wall-column 0 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                     (place right-wall-column 0.02 (translate [-1 -1 1] (wall-sphere-bottom 1)))
                     (key-place 5 0 web-post-tr))
               (hull (place right-wall-column 4 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                     (place right-wall-column 4 (translate [-1 1 1] (wall-sphere-bottom 0)))
                     (key-place 5 4 web-post-br)))])))))

(def left-wall
  (let [place case-place]
    (union
     (apply union
            (for [x (range-inclusive -1 (- 1.6666 wall-step) wall-step)]
              (hull (place left-wall-column x wall-sphere-top-front)
                    (place left-wall-column (+ x wall-step) wall-sphere-top-front)
                    (place left-wall-column x wall-sphere-bottom-front)
                    (place left-wall-column (+ x wall-step) wall-sphere-bottom-front))))
     (hull (place left-wall-column -1 wall-sphere-top-front)
           (place left-wall-column -1 wall-sphere-bottom-front)
           (place left-wall-column 0.02 wall-sphere-top-back)
           (place left-wall-column 0.02 wall-sphere-bottom-back))
     (hull (place left-wall-column 0 (translate [1 -1 1] wall-sphere-bottom-back))
           (place left-wall-column 1 (translate [1 0 1] wall-sphere-bottom-back))
           (key-place 0 0 web-post-tl)
           (key-place 0 0 web-post-bl))
     (hull (place left-wall-column 1 (translate [1 0 1] wall-sphere-bottom-back))
           (place left-wall-column 2 (translate [1 0 1] wall-sphere-bottom-back))
           (key-place 0 0 web-post-bl)
           (key-place 0 1 web-post-bl))
     (hull (place left-wall-column 2 (translate [1 0 1] wall-sphere-bottom-back))
           (place left-wall-column 1.6666  (translate [1 0 1] wall-sphere-bottom-front))
           (key-place 0 1 web-post-bl)
           (key-place 0 2 web-post-bl))
     (hull (place left-wall-column 1.6666  (translate [1 0 1] wall-sphere-bottom-front))
           (key-place 0 2 web-post-bl)
           (key-place 0 3 web-post-tl))
     (hull (place left-wall-column 1.6666  (translate [1 0 1] wall-sphere-bottom-front))
           (thumb-place 1 1 web-post-tr)
           (key-place 0 3 web-post-tl))
     (hull (place left-wall-column 1.6666 (translate [1 0 1] wall-sphere-bottom-front))
           (thumb-place 1 1 web-post-tr)
           (thumb-place 1/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))))))

(def thumb-back-wall
  (let [step wall-step
        top-step 0.05
        front-top-cover (fn [x-start x-end y-start y-end]
                          (apply union
                                 (for [x (range-inclusive x-start (- x-end top-step) top-step)
                                       y (range-inclusive y-start (- y-end top-step) top-step)]
                                   (hull (thumb-place x y wall-sphere-top-back)
                                         (thumb-place (+ x top-step) y wall-sphere-top-back)
                                         (thumb-place x (+ y top-step) wall-sphere-top-back)
                                         (thumb-place (+ x top-step) (+ y top-step) wall-sphere-top-back)))))
        back-y thumb-back-y]
    (union
     (apply union
            (for [x (range-inclusive 1/2 (- (+ 5/2 0.05) step) step)]
              (hull (thumb-place x back-y wall-sphere-top-back)
                    (thumb-place (+ x step) back-y wall-sphere-top-back)
                    (thumb-place x back-y wall-sphere-bottom-back)
                    (thumb-place (+ x step) back-y wall-sphere-bottom-back))))
     (hull (thumb-place 1/2 back-y wall-sphere-top-back)
           (thumb-place 1/2 back-y wall-sphere-bottom-back)
           (case-place left-wall-column 1.6666 wall-sphere-top-front))
     (hull (thumb-place 1/2 back-y wall-sphere-bottom-back)
           (case-place left-wall-column 1.6666 wall-sphere-top-front)
           (case-place left-wall-column 1.6666 wall-sphere-bottom-front))
     (hull
      (thumb-place 1/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
      (thumb-place 1 1 web-post-tr)
      (thumb-place 3/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
      (thumb-place 1 1 web-post-tl))
     (hull
      (thumb-place (+ 5/2 0.05) thumb-back-y (translate [1 -1 1] wall-sphere-bottom-back))
      (thumb-place 3/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
      (thumb-place 1 1 web-post-tl)
      (thumb-place 2 1 web-post-tl)))))

(def thumb-left-wall
  (let [step wall-step
        place thumb-place]
    (union
     (apply union
            (for [x (range-inclusive (+ -1 0.07) (- 1.95 step) step)]
              (hull (place thumb-left-wall-column x wall-sphere-top-front)
                    (place thumb-left-wall-column (+ x step) wall-sphere-top-front)
                    (place thumb-left-wall-column x wall-sphere-bottom-front)
                    (place thumb-left-wall-column (+ x step) wall-sphere-bottom-front))))
     (hull (place thumb-left-wall-column 1.95 wall-sphere-top-front)
           (place thumb-left-wall-column 1.95 wall-sphere-bottom-front)
           (place thumb-left-wall-column thumb-back-y wall-sphere-top-back)
           (place thumb-left-wall-column thumb-back-y wall-sphere-bottom-back))

     (hull
      (thumb-place thumb-left-wall-column thumb-back-y (translate [1 -1 1] wall-sphere-bottom-back))
      (thumb-place thumb-left-wall-column 0 (translate [1 0 1] wall-sphere-bottom-back))
      (thumb-place 2 1 web-post-tl)
      (thumb-place 2 1 web-post-bl))
     (hull
      (thumb-place thumb-left-wall-column 0 (translate [1 0 1] wall-sphere-bottom-back))
      (thumb-place 2 0 web-post-tl)
      (thumb-place 2 1 web-post-bl))
     (hull
      (thumb-place thumb-left-wall-column 0 (translate [1 0 1] wall-sphere-bottom-back))
      (thumb-place thumb-left-wall-column -1 (translate [1 0 1] wall-sphere-bottom-back))
      (thumb-place 2 0 web-post-tl)
      (thumb-place 2 0 web-post-bl))
     (hull
      (thumb-place thumb-left-wall-column -1 (translate [1 0 1] wall-sphere-bottom-back))
      (thumb-place 2 -1 web-post-tl)
      (thumb-place 2 0 web-post-bl))
     (hull
      (thumb-place thumb-left-wall-column -1 (translate [1 0 1] wall-sphere-bottom-back))
      (thumb-place thumb-left-wall-column (+ -1 0.07) (translate [1 1 1] wall-sphere-bottom-front))
      (thumb-place 2 -1 web-post-tl)
      (thumb-place 2 -1 web-post-bl)))))

(def thumb-front-wall
  (let [step wall-step ;;0.1
        wall-sphere-top-fronttep 0.05 ;;0.05
        place thumb-place
        plate-height (/ (- sa-double-length mount-height) 2)
        thumb-tl (->> web-post-tl
                      (translate [0 plate-height 0]))
        thumb-bl (->> web-post-bl
                      (translate [0 (- plate-height) 0]))
        thumb-tr (->> web-post-tr
                      (translate [-0 plate-height 0]))
        thumb-br (->> web-post-br
                      (translate [-0 (- plate-height) 0]))]
    (union
     (apply union
            (for [x (range-inclusive thumb-right-wall (- (+ 5/2 0.05) step) step)]
              (hull (place x thumb-front-row wall-sphere-top-front)
                    (place (+ x step) thumb-front-row wall-sphere-top-front)
                    (place x thumb-front-row wall-sphere-bottom-front)
                    (place (+ x step) thumb-front-row wall-sphere-bottom-front))))

     (hull (place thumb-right-wall thumb-front-row wall-sphere-top-front)
           (place thumb-right-wall thumb-front-row wall-sphere-bottom-front)
           (case-place 0.5 4 wall-sphere-top-front))
     (hull (place thumb-right-wall thumb-front-row wall-sphere-bottom-front)
           (case-place 0.5 4 wall-sphere-top-front)
           (case-place 0.7 4 wall-sphere-bottom-front))

     (hull (place thumb-right-wall thumb-front-row wall-sphere-bottom-front)
           (key-place 1 4 web-post-bl)
           (place 0 -1/2 thumb-br)
           (place 0 -1/2 web-post-br)
           (case-place 0.7 4 wall-sphere-bottom-front))

     (hull (place (+ 5/2 0.05) thumb-front-row (translate [1 1 1] wall-sphere-bottom-front))
           (place (+ 3/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
           (place 2 -1 web-post-bl)
           (place 2 -1 web-post-br))

     (hull (place thumb-right-wall thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
           (place (+ 1/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
           (place 0 -1/2 thumb-bl)
           (place 0 -1/2 thumb-br))
     (hull (place (+ 1/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
           (place (+ 3/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
           (place 0 -1/2 thumb-bl)
           (place 1 -1/2 thumb-bl)
           (place 1 -1/2 thumb-br)
           (place 2 -1 web-post-br)))))

(def new-case-fingers
  (union front-wall
         right-wall
         back-wall
         left-wall))

(def new-case-thumb
  (union thumb-back-wall
         thumb-left-wall
         thumb-front-wall))

;;;;;;;;;;;;
;; Bottom ;;
;;;;;;;;;;;;


(def bottom-key-guard (->> (cube mount-width mount-height web-thickness)
                           (translate [0 0 (+ (- (/ web-thickness 2)) -4.5)])))
(def bottom-front-key-guard (->> (cube mount-width (/ mount-height 2) web-thickness)
                                 (translate [0 (/ mount-height 4) (+ (- (/ web-thickness 2)) -4.5)])))

(def bottom-plate
  (union
   (apply union
          (for [column columns
                row (drop-last rows)
                :when (finger-has-key-place-p row column)]
            (->> bottom-key-guard
                 (key-place column row))))
   (thumb-layout (rotate (/ π 2) [0 0 1] bottom-key-guard))
   (apply union
          (for [column columns
                row [(last rows)]
                :when (finger-has-key-place-p row column)]
            (->> bottom-front-key-guard
                 (key-place column row))))
   (let [shift #(translate [0 0 (+ (- web-thickness) -5)] %)
         web-post-tl (shift web-post-tl)
         web-post-tr (shift web-post-tr)
         web-post-br (shift web-post-br)
         web-post-bl (shift web-post-bl)
         half-shift-correction #(translate [0 (/ mount-height 2) 0] %)
         half-post-br (half-shift-correction web-post-br)
         half-post-bl (half-shift-correction web-post-bl)
         row-connections (concat
                          (for [column (drop-last columns)
                                row (drop-last rows)
                                :when (not (or (and (= column 0)
                                                    (= row 4))
                                               (and (= column -1)
                                                    (= row 3))))]
                            (triangle-hulls
                             (key-place (inc column) row web-post-tl)
                             (key-place column row web-post-tr)
                             (key-place (inc column) row web-post-bl)
                             (key-place column row web-post-br)))
                          (for [column (drop-last columns)
                                row [(last rows)]
                                :when (or (not= column 0)
                                          (not= row 4))]
                            (triangle-hulls
                             (key-place (inc column) row web-post-tl)
                             (key-place column row web-post-tr)
                             (key-place (inc column) row half-post-bl)
                             (key-place column row half-post-br))))
         column-connections (for [column columns
                                  row (drop-last rows)
                                  :when (or (not= column 0)
                                            (not= row 3))]
                              (triangle-hulls
                               (key-place column row web-post-bl)
                               (key-place column row web-post-br)
                               (key-place column (inc row) web-post-tl)
                               (key-place column (inc row) web-post-tr)))
         diagonal-connections (for [column (drop-last columns)
                                    row (drop-last rows)
                                    :when (or (not= column 0)
                                              (not= row 3))]
                                (triangle-hulls
                                 (key-place column row web-post-br)
                                 (key-place column (inc row) web-post-tr)
                                 (key-place (inc column) row web-post-bl)
                                 (key-place (inc column) (inc row) web-post-tl)))
         main-keys-bottom (concat row-connections
                                  column-connections
                                  diagonal-connections)
         front-wall (concat
                     (for [x (range 2 5)]
                       (union
                        (hull (case-place (- x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                              (case-place (+ x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                              (key-place x 4 half-post-bl)
                              (key-place x 4 half-post-br))
                        (hull (case-place (- x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                              (key-place x 4 half-post-bl)
                              (key-place (- x 1) 4 half-post-br))))
                     [(hull (case-place right-wall-column 4 (translate [0 1 1] wall-sphere-bottom-front))
                            (case-place (- right-wall-column 1) 4 (translate [0 1 1] wall-sphere-bottom-front))
                            (key-place 5 4 half-post-bl)
                            (key-place 5 4 half-post-br))
                      (hull (case-place (+ 4 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                            (case-place (- right-wall-column 1) 4 (translate [0 1 1] wall-sphere-bottom-front))
                            (key-place 4 4 half-post-br)
                            (key-place 5 4 half-post-bl))])
         right-wall (concat
                     (for [x (range 0 4)]
                       (hull (case-place right-wall-column x (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (key-place 5 x web-post-br)
                             (key-place 5 x web-post-tr)))
                     (for [x (range 0 4)]
                       (hull (case-place right-wall-column x (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (case-place right-wall-column (inc x) (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (key-place 5 x web-post-br)
                             (key-place 5 (inc x) web-post-tr)))
                     [(union
                       (hull (case-place right-wall-column 0 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (case-place right-wall-column 0.02 (translate [-1 -1 1] (wall-sphere-bottom 1)))
                             (key-place 5 0 web-post-tr)
                             )
                       (hull (case-place right-wall-column 4 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (case-place right-wall-column 4 (translate [0 1 1] (wall-sphere-bottom 0)))
                             (key-place 5 4 half-post-br)
                             )
                       (hull (case-place right-wall-column 4 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (key-place 5 4 half-post-br)
                             (key-place 5 4 web-post-tr)))])
         back-wall (concat
                    (for [x (range 1 6)]
                      (union
                       (hull (case-place (- x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                             (case-place (+ x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                             (key-place x 0 web-post-tl)
                             (key-place x 0 web-post-tr))
                       (hull (case-place (- x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                             (key-place x 0 web-post-tl)
                             (key-place (- x 1) 0 web-post-tr))))
                    [(hull (case-place left-wall-column 0 (translate [1 -1 1] wall-sphere-bottom-back))
                           (case-place (+ left-wall-column 1) 0  (translate [0 -1 1] wall-sphere-bottom-back))
                           (key-place 0 0 web-post-tl)
                           (key-place 0 0 web-post-tr))])
         left-wall (let [place case-place]
                     [(hull (place left-wall-column 0 (translate [1 -1 1] wall-sphere-bottom-back))
                            (place left-wall-column 1 (translate [1 0 1] wall-sphere-bottom-back))
                            (key-place 0 0 web-post-tl)
                            (key-place 0 0 web-post-bl))
                      (hull (place left-wall-column 1 (translate [1 0 1] wall-sphere-bottom-back))
                            (place left-wall-column 2 (translate [1 0 1] wall-sphere-bottom-back))
                            (key-place 0 0 web-post-bl)
                            (key-place 0 1 web-post-bl))
                      (hull (place left-wall-column 2 (translate [1 0 1] wall-sphere-bottom-back))
                            (place left-wall-column 1.6666  (translate [1 0 1] wall-sphere-bottom-front))
                            (key-place 0 1 web-post-bl)
                            (key-place 0 2 web-post-bl))
                      (hull (place left-wall-column 1.6666  (translate [1 0 1] wall-sphere-bottom-front))
                            (key-place 0 2 web-post-bl)
                            (key-place 0 3 web-post-tl))])
         thumbs [(hull (thumb-place 0 -1/2 web-post-bl)
                       (thumb-place 0 -1/2 web-post-tl)
                       (thumb-place 1 -1/2 web-post-tr)
                       (thumb-place 1 -1/2 web-post-br))
                 (hull (thumb-place 1 -1/2 web-post-tr)
                       (thumb-place 1 -1/2 web-post-tl)
                       (thumb-place 1 1 web-post-bl)
                       (thumb-place 1 1 web-post-br))
                 (hull (thumb-place 2 -1 web-post-tr)
                       (thumb-place 2 -1 web-post-tl)
                       (thumb-place 2 0 web-post-bl)
                       (thumb-place 2 0 web-post-br))
                 (hull (thumb-place 2 0 web-post-tr)
                       (thumb-place 2 0 web-post-tl)
                       (thumb-place 2 1 web-post-bl)
                       (thumb-place 2 1 web-post-br))
                 (triangle-hulls (thumb-place 2 1 web-post-tr)
                                 (thumb-place 1 1 web-post-tl)
                                 (thumb-place 2 1 web-post-br)
                                 (thumb-place 1 1 web-post-bl)
                                 (thumb-place 2 0 web-post-tr)
                                 (thumb-place 1 -1/2 web-post-tl)
                                 (thumb-place 2 0 web-post-br)
                                 (thumb-place 1 -1/2 web-post-bl)
                                 (thumb-place 2 -1 web-post-tr)
                                 (thumb-place 2 -1 web-post-br))
                 (hull (thumb-place 2 -1 web-post-br)
                       (thumb-place 1 -1/2 web-post-bl)
                       (thumb-place 1 -1 web-post-bl))
                 (hull (thumb-place 1 -1/2 web-post-bl)
                       (thumb-place 1 -1 web-post-bl)
                       (thumb-place 1 -1/2 web-post-br)
                       (thumb-place 1 -1 web-post-br))
                 (hull (thumb-place 0 -1/2 web-post-bl)
                       (thumb-place 0 -1 web-post-bl)
                       (thumb-place 0 -1/2 web-post-br)
                       (thumb-place 0 -1 web-post-br))
                 (hull (thumb-place 0 -1/2 web-post-bl)
                       (thumb-place 0 -1 web-post-bl)
                       (thumb-place 1 -1/2 web-post-br)
                       (thumb-place 1 -1 web-post-br))]
         thumb-back-wall [(hull
                           (thumb-place 1/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
                           (thumb-place 1 1 web-post-tr)
                           (thumb-place 3/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
                           (thumb-place 1 1 web-post-tl))

                          (hull
                           (thumb-place (+ 5/2 0.05) thumb-back-y (translate [1 -1 1] wall-sphere-bottom-back))
                           (thumb-place 3/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
                           (thumb-place 1 1 web-post-tl)
                           (thumb-place 2 1 web-post-tl))
                          (hull
                           (thumb-place 1/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
                           (case-place left-wall-column 1.6666 (translate [1 0 1] wall-sphere-bottom-front))
                           (key-place 0 3 web-post-tl)
                           (thumb-place 1 1 web-post-tr))
                          ]
         thumb-left-wall [(hull
                           (thumb-place thumb-left-wall-column thumb-back-y (translate [1 -1 1] wall-sphere-bottom-back))
                           (thumb-place thumb-left-wall-column 0 (translate [1 0 1] wall-sphere-bottom-back))
                           (thumb-place 2 1 web-post-tl)
                           (thumb-place 2 1 web-post-bl))
                          (hull
                           (thumb-place thumb-left-wall-column 0 (translate [1 0 1] wall-sphere-bottom-back))
                           (thumb-place 2 0 web-post-tl)
                           (thumb-place 2 1 web-post-bl))
                          (hull
                           (thumb-place thumb-left-wall-column 0 (translate [1 0 1] wall-sphere-bottom-back))
                           (thumb-place thumb-left-wall-column -1 (translate [1 0 1] wall-sphere-bottom-back))
                           (thumb-place 2 0 web-post-tl)
                           (thumb-place 2 0 web-post-bl))
                          (hull
                           (thumb-place thumb-left-wall-column -1 (translate [1 0 1] wall-sphere-bottom-back))
                           (thumb-place 2 -1 web-post-tl)
                           (thumb-place 2 0 web-post-bl))
                          (hull
                           (thumb-place thumb-left-wall-column -1 (translate [1 0 1] wall-sphere-bottom-back))
                           (thumb-place thumb-left-wall-column (+ -1 0.07) (translate [1 1 1] wall-sphere-bottom-front))
                           (thumb-place 2 -1 web-post-tl)
                           (thumb-place 2 -1 web-post-bl))]
         thumb-front-wall [(hull (thumb-place (+ 5/2 0.05) thumb-front-row (translate [1 1 1] wall-sphere-bottom-front))
                                 (thumb-place (+ 3/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 2 -1 web-post-bl)
                                 (thumb-place 2 -1 web-post-br))
                           (hull (thumb-place (+ 1/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place (+ 3/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 0 -1 web-post-bl)
                                 (thumb-place 1 -1 web-post-bl)
                                 (thumb-place 1 -1 web-post-br)
                                 (thumb-place 2 -1 web-post-br))
                           (hull (thumb-place thumb-right-wall thumb-front-row (translate [-1 1 1] wall-sphere-bottom-front))
                                 (thumb-place (+ 1/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 0 -1 web-post-bl)
                                 (thumb-place 0 -1 web-post-br))]
         thumb-inside [(triangle-hulls
                        (thumb-place 1 1 web-post-tr)
                        (key-place 0 3 web-post-tl)
                        (thumb-place 1 1 web-post-br)
                        (key-place 0 3 web-post-bl)
                        (thumb-place 1 -1/2 web-post-tr)
                        (thumb-place 0 -1/2 web-post-tl)
                        (key-place 0 3 web-post-bl)
                        (thumb-place 0 -1/2 web-post-tr)
                        (key-place 0 3 web-post-br)
                        (key-place 1 3 web-post-bl)
                        (thumb-place 0 -1/2 web-post-tr)
                        (key-place 1 4 web-post-tl)
                        (key-place 1 4 half-post-bl))

                       (hull
                        (thumb-place 0 -1/2 web-post-tr)
                        (thumb-place 0 -1/2 web-post-br)
                        (key-place 1 4 half-post-bl))

                       (hull
                        (key-place 1 4 half-post-bl)
                        (key-place 1 4 half-post-br)
                        (case-place (- 2 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                        (case-place 0.7 4 (translate [0 1 1] wall-sphere-bottom-front)))

                       (hull
                        (thumb-place 0 -1 web-post-br)
                        (thumb-place 0 -1/2 web-post-br)
                        (thumb-place thumb-right-wall thumb-front-row (translate [-1 1 1] wall-sphere-bottom-front))
                        (key-place 1 4 (translate [0 0 8.5] web-post-bl))
                        (key-place 1 4 half-post-bl)
                        )]
         stands (let [bumper-diameter 9.6
                      bumper-radius (/ bumper-diameter 2)
                      stand-diameter (+ bumper-diameter 2)
                      stand-radius (/ stand-diameter 2)
                      stand-at #(difference (->> (sphere stand-radius)
                                                 (translate [0 0 (+ (/ stand-radius -2) -4.5)])
                                                 %
                                                 (bottom-hull))
                                            (->> (cube stand-diameter stand-diameter stand-radius)
                                                 (translate [0 0 (/ stand-radius -2)])
                                                 %)
                                            (->> (sphere bumper-radius)
                                                 (translate [0 0 (+ (/ stand-radius -2) -4.5)])
                                                 %
                                                 (bottom 1.5)))]
                  [(stand-at #(key-place 0 1 %))
                   (stand-at #(thumb-place 1 -1/2 %))
                   (stand-at #(key-place 5 0 %))
                   (stand-at #(key-place 5 3 %))])]
     (apply union
            (concat
             main-keys-bottom
             front-wall
             right-wall
             back-wall
             left-wall
             thumbs
             thumb-back-wall
             thumb-left-wall
             thumb-front-wall
             thumb-inside
             stands)))))

(def screw-hole (->> (cylinder 1.5 60)
                     (translate [0 0 3/2])
                     (with-fn wall-sphere-n)))

(def screw-holes
  (union
   (key-place (+ 4 1/2) 1/2 screw-hole)
   (key-place (+ 4 1/2) (+ 3 1/2) screw-hole)
   (thumb-place 2 -1/2 screw-hole)))

(defn circuit-cover [width length height]
  (let [cover-sphere-radius 1
        cover-sphere (->> (sphere cover-sphere-radius)
                          (with-fn 20))
        cover-sphere-z (+ (- height) (- cover-sphere-radius))
        cover-sphere-x (+ (/ width 2) cover-sphere-radius)
        cover-sphere-y (+ (/ length 2) (+ cover-sphere-radius))
        cover-sphere-tl (->> cover-sphere
                             (translate [(- cover-sphere-x) (- cover-sphere-y) cover-sphere-z])
                             (key-place 1/2 3/2))
        cover-sphere-tr (->> cover-sphere
                             (translate [cover-sphere-x (- cover-sphere-y) cover-sphere-z])
                             (key-place 1/2 3/2))
        cover-sphere-br (->> cover-sphere
                             (translate [cover-sphere-x cover-sphere-y cover-sphere-z])
                             (key-place 1/2 3/2))
        cover-sphere-bl (->> cover-sphere
                             (translate [(- cover-sphere-x) cover-sphere-y cover-sphere-z])
                             (key-place 1/2 3/2))

        lower-to-bottom #(translate [0 0 (+ (- cover-sphere-radius) -5.5)] %)
        bl (->> cover-sphere lower-to-bottom (key-place 0 1/2))
        br (->> cover-sphere lower-to-bottom (key-place 1 1/2))
        tl (->> cover-sphere lower-to-bottom (key-place 0 5/2))
        tr (->> cover-sphere lower-to-bottom (key-place 1 5/2))

        mlb (->> cover-sphere
                 (translate [(- cover-sphere-x) 0 (+ (- height) -1)])
                 (key-place 1/2 3/2))
        mrb (->> cover-sphere
                 (translate [cover-sphere-x 0 (+ (- height) -1)])
                 (key-place 1/2 3/2))

        mlt (->> cover-sphere
                 (translate [(+ (- cover-sphere-x) -4) 0 -6])
                 (key-place 1/2 3/2))
        mrt (->> cover-sphere
                 (translate [(+ cover-sphere-x 4) 0 -6])
                 (key-place 1/2 3/2))]
    (union
     (hull cover-sphere-bl cover-sphere-br cover-sphere-tl cover-sphere-tr)
     (hull cover-sphere-br cover-sphere-bl bl br)
     (hull cover-sphere-tr cover-sphere-tl tl tr)
     (hull cover-sphere-tl tl mlb mlt)
     (hull cover-sphere-bl bl mlb mlt)
     (hull cover-sphere-tr tr mrb mrt)
     (hull cover-sphere-br br mrb mrt))))

(def io-exp-width 10)
(def io-exp-height 8)
(def io-exp-length 36)

(def teensy-width 20)
(def teensy-height 12)
(def teensy-length 40)

(def io-exp-cover (circuit-cover io-exp-width io-exp-length io-exp-height))
(def teensy-cover (circuit-cover teensy-width teensy-length teensy-height))

(def trrs-diameter 6.6)
(def trrs-radius (/ trrs-diameter 2))
(def trrs-hole-depth 10)

(def trrs-hole (->> (union (cylinder trrs-radius trrs-hole-depth)
                           (->> (cube trrs-diameter (+ trrs-radius 5) trrs-hole-depth)
                                (translate [0 (/ (+ trrs-radius 5) 2) 0])))
                    (rotate (/ π 2) [1 0 0])
                    (translate [0 (+ (/ mount-height 2) 4) (- trrs-radius)])
                    (with-fn 50)))

(def trrs-hole-just-circle
  (->> (cylinder trrs-radius trrs-hole-depth)
       (rotate (/ π 2) [1 0 0])
       (translate [0 (+ (/ mount-height 2) 4) (- trrs-radius)])
       (with-fn 50)
       (key-place 1/2 0)))

(def trrs-box-hole (->> (cube 14 14 7 )
                        (translate [0 1 -3.5])))


(def trrs-cutout
  (->> (union trrs-hole
              trrs-box-hole)
       (key-place 1/2 0)))

(def teensy-pcb-thickness 1.6)
(def teensy-offset-height 5)

(def teensy-pcb (->> (cube 18 30.5 teensy-pcb-thickness)
                     (translate [0 0 (+ (/ teensy-pcb-thickness -2) (- teensy-offset-height))])
                     (key-place 1/2 3/2)
                     (color [1 0 0])))

(def teensy-support
  (difference
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
               (translate [0 (/ 30.5 -2) (+ (- teensy-offset-height)
                                            #_(/ (+ teensy-pcb-thickness 3) -2)
                                            )])
               (key-place 1/2 3/2)
               (color [0 0 1]))))
   teensy-pcb
   (->> (cube 18 30.5 teensy-pcb-thickness)
        (translate [0 1.5 (+ (/ teensy-pcb-thickness -2) (- teensy-offset-height) -1)])
        (key-place 1/2 3/2)
        (color [1 0 0]))))

(def usb-cutout
  (let [hole-height 6.2
        side-radius (/ hole-height 2)
        hole-width 10.75
        side-cylinder (->> (cylinder side-radius teensy-length)
                           (with-fn 20)
                           (translate [(/ (- hole-width hole-height) 2) 0 0]))]
    (->> (hull side-cylinder
               (mirror [-1 0 0] side-cylinder))
         (rotate (/ π 2) [1 0 0])
         (translate [0 (/ teensy-length 2) (- side-radius)])
         (translate [0 0 (- 1)])
         (translate [0 0 (- teensy-offset-height)])
         (key-place 1/2 3/2))))

;;;;;;;;;;;;;;;;;;
;; Glue Joints  ;;
;;;;;;;;;;;;;;;;;;

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

(def dactyl-bottom-right
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

(def dactyl-bottom-left
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
      (difference
       (apply union pieces-of-this-piece)
       trrs-hole-just-circle
       screw-holes))))

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

(defn untent [shape] (rotate (- tenting-angle) [0 1 0] shape))
(defn retent [shape] (rotate tenting-angle [0 1 0] shape))

; badly named.
(defn finger-top-outline-prism [distance-below narrow-percent]
  (let [untented-key-blanks (untent key-blanks-pieces)
        narrower-key-blanks (scale [(/ (- 100 narrow-percent) 100)
                                    (/ (- 100 narrow-percent) 100)
                                    1] untented-key-blanks)
        adjusted (scale [(/ (+ distance-below column-radius) column-radius)
                         (/ (+ distance-below row-radius) row-radius)
                         1] narrower-key-blanks)
        adjusted-above (scale [(/ (- column-radius distance-below) column-radius)
                               (/ (- row-radius distance-below) row-radius)
                              1] narrower-key-blanks)]
    (retent
     (hull
      (translate [0 0 distance-below] adjusted-above)
      (translate [0 0 (- distance-below)] adjusted)))))

(defn finger-top-outline-prism2 [distance-below narrow-percent]
  (let [pyramid (fn [shape]
                  (let [below (scale [(/ (+ column-radius distance-below) column-radius)
                                      (/ (+ row-radius distance-below) row-radius)
                                      1] shape)
                        above (scale [(/ (- column-radius distance-below) column-radius)
                                      (/ (- row-radius distance-below) row-radius)
                                      1] shape)]
                    (hull
                     (translate [0 0 distance-below] above)
                     (translate [0 0 (- distance-below)] below))))]
    (apply union
           (for [column columns
                 row rows
                 :when (finger-has-key-place-p row column)]
             (->> chosen-blank-single-plate
                  (scale [(/ (- 100 narrow-percent) 100)
                          (/ (- 100 narrow-percent) 100)
                          1])
                  (key-place column row)
                  untent
                  pyramid
                  retent)))))


(defn finger-case-bottom-sphere [flatness downness]
  "flatness ill-understood; 0-120 seems valid. downness is how far down the thing is from the top case."
  (let [
        sph-row-radius (+ flatness row-radius)
        sph-column-radius (+ flatness column-radius)]
    (->> (sphere sph-row-radius)
                                        ; i don't see why this ratio
                                        ; is scaled by half
         (scale [(/ (/ sph-column-radius sph-row-radius) 2) 1 1])
         (color [0 0.7 0.7 0.8])
         (translate [0 0 sph-row-radius])
                                        ; tenting is pi over 12; outer
                                        ; rows raised so rotate a little less
         (rotate (* (/ π 12) 0.85) [0 1 0])
         (translate [0 0 (- sph-row-radius)])
         (translate [0 0 row-radius])
         (translate [0 0 (- flatness downness)]))))

(defn finger-case-bottom-shell [flatness downness thickness]
  (let [the-sphere (finger-case-bottom-sphere flatness downness)
        the-shell (difference the-sphere (translate [0 0 thickness] the-sphere))
        distance-below-to-intersect (max (+ downness flatness) 20)
        big-intersection-shape (finger-top-outline-prism distance-below-to-intersect 0)]
    (intersection the-shell big-intersection-shape)))

(defn big-marshmallowy-sides [flatness downness thickness radius]
  (let [
        the-sphere (finger-case-bottom-sphere flatness downness)
        outline-thickness 1
        gasket-sphere-fn 9 ; detail of sphere. normally 20 or so?
                           ; severe performance impact. for me, with
                           ; openscad 2015.03-2, this looked like big
                           ; delays with lots of memory usage after
                           ; the progress bar got to 1000.
        the-shell (difference the-sphere (translate [0 0 thickness] the-sphere))
        thick-shell (difference (translate [0 0 (- thickness)] the-sphere)
                                (translate [0 0 (* 2 thickness)] the-sphere))
        distance-below-to-intersect (max (+ downness flatness) 20)
        big-intersection-shape (finger-top-outline-prism distance-below-to-intersect 0)
        little-intersection-shape (finger-top-outline-prism distance-below-to-intersect 2)
        finger-case-outline (fn [flatness downness]
                              (difference (intersection the-shell big-intersection-shape)
                                          (intersection thick-shell little-intersection-shape)))
        the-outline (finger-case-outline flatness downness)
        marshmallow-gasket (fn [r] (minkowski
                                    (finger-case-outline flatness downness)
                                    (binding [*fn* gasket-sphere-fn]
                                      (sphere r))))
        sides (difference
               (difference (marshmallow-gasket radius)
                           (marshmallow-gasket (- radius thickness)))
               big-intersection-shape)]
    sides))

(spit "things/dactyl-blank-all.scad"
      (write-scad
       (union
        (apply union key-blanks-pieces)
        #_(color [1 0 0 0.5] (finger-top-outline-prism 30 0))
        (color [0 1 0 0.7] (finger-top-outline-prism2 30 0)))


       #_(union dactyl-top-right-thumb
              (apply union (dactyl-top-right-pieces key-blanks-pieces))
              (binding [*fn* 12]
                (union
                 (finger-case-bottom-shell 40 19 3)
                 (big-marshmallowy-sides 40 0 3 19)))
              caps)))

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
