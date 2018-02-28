(ns dactyl-keyboard.dactyl
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [dactyl-keyboard.switch-hole :refer :all]
            [dactyl-keyboard.keycaps :refer :all]
            [dactyl-keyboard.placement :refer :all]
            [dactyl-keyboard.frame-glue-joint :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.half-circle-connectors :refer :all]
            [dactyl-keyboard.adafruit-usb :refer :all]))



; we split the keyholes into pieces for smaller print volume and quicker printing
(def columns-pieces [(range -1 0) (range 0 2) (range 2 4) (range 4 6)])
(def columns   (apply concat columns-pieces))
(def rows (range 0 5))

                                        ; this defines the keys
                                        ; missing from the finger part
                                        ; that make room for the thumb
(defn finger-has-key-place-p [row column]
  (cond
    (and (= column 0) (= row 4)) false
    (and (= column -1) (= row 4)) false
    true true))

                                        ; coordinates of keys around
                                        ; the edge of the finger
                                        ;               >*****v
                                        ;               *.....*
                                        ;               *.....*
                                        ; start here -> ^**...*
                                        ;               xx****<

(def around-edge [[-1 3] [-1 2] [-1 1] [-1 0]
                  [0 0] [1 0] [2 0] [3 0] [4 0] [5 0]
                  [5 1] [5 2] [5 3] [5 4]
                  [4 4] [3 4] [2 4] [1 4]
                  [1 3] [0 3]])
(def around-edge-rot1 (concat (rest around-edge) (list (first around-edge))))


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

(defn row-connector [row column]
  (triangle-hulls
     (key-place (inc column) row web-post-tl)
     (key-place column row web-post-tr)
     (key-place (inc column) row web-post-bl)
     (key-place column row web-post-br)))
(defn row-connector-untranslate [shape]
  (translate [(- (- (/ mount-width 2) post-adj)) 0 0] shape))
(defn row-connector-retranslate [shape]
  (translate [(- (/ mount-width 2) post-adj) 0 0] shape))

(defn row-connectors [column]
  (for [row rows
        :when (finger-has-key-place-p row column)]
    (row-connector row column)))

(defn diagonal-connector [row column]
  (triangle-hulls
     (key-place column row web-post-br)
     (key-place column (inc row) web-post-tr)
     (key-place (inc column) row web-post-bl)
     (key-place (inc column) (inc row) web-post-tl)))
(defn diagonal-connector-untranslate [shape]
  (translate [(- (- (/ mount-width 2) post-adj))
              (- (- (/ mount-height 2) post-adj))
              0] shape))
(defn diagonal-connector-retranslate [shape]
  (translate [(- (/ mount-width 2) post-adj)
              (- (/ mount-height 2) post-adj)
              0] shape))

(defn diagonal-connectors [column]
  (for [row (drop-last rows)
        :when (and
               (finger-has-key-place-p (inc row) (inc column))
               (not (thumb-glue-joint-left-of-p (inc row) (inc column))))]
    (diagonal-connector row column)))

(defn column-connector [row column]
  (triangle-hulls
     (key-place column row web-post-bl)
     (key-place column row web-post-br)
     (key-place column (inc row) web-post-tl)
     (key-place column (inc row) web-post-tr)))
(defn column-connector-untranslate [shape]
  (translate [0 (- (- (/ mount-height 2) post-adj)) 0] shape))
(defn column-connector-retranslate [shape]
  (translate [0 (- (/ mount-height 2) post-adj) 0] shape))

(defn column-connectors [column]
  (for [row (drop-last rows)
        :when (finger-has-key-place-p (inc row) column)]
    (column-connector row column)))

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
  (let [
                                        ; these are the same as above;
                                        ; commenting out. we will use
                                        ; row-radius and column-radius
                                        ; in thumb-case-bottom-sphere

        ;; α (/ π 12)
        ;; row-radius (+ (/ (/ (+ mount-height 1) 2)
                         ;; (Math/sin (/ α 2)))
                      ;; cap-top-height)
        ;; β (/ π 36)
        ;; column-radius (+ (/ (/ (+ mount-width 2) 2)
                            ;; (Math/sin (/ β 2)))
                         ;; cap-top-height)
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
         ; axis. that will change what the translation after it
         ; needs to be when you change tenting-angle.
         (rotate (/ π 12) [1 1 0])
         (translate [-53 -45 40]))))

(defn thumb-untent [shape]
  (->> shape
       (translate [53 45 -40])
       (rotate (- (/ π 12)) [1 1 0])))
(defn thumb-retent [shape]
  (->> shape
       (rotate (/ π 12) [1 1 0])
       (translate [-53 -45 40])))

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

(def double-plates-blank
  (let [plate-height (/ (- sa-double-length mount-height) 2)
        top-plate (->> (cube mount-width plate-height web-thickness)
                       (translate [0 (/ (+ plate-height mount-height) 2)
                                   (- plate-thickness (/ web-thickness 2))]))]
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

(def thumb-blanks
  (union
   (thumb-layout (rotate (/ π 2) [0 0 1] chosen-blank-single-plate))
   (thumb-place 0 -1/2 double-plates-blank)
   (thumb-place 1 -1/2 double-plates-blank)))


;;;;;;;;;;
;; Case ;;
;;;;;;;;;;

(def marshmallowy-sides-flatness 40) ; see flatness comments below
(def marshmallowy-sides-downness -10)
(def marshmallowy-sides-thickness 3)
(def marshmallowy-sides-radius 19)
(def thumb-sides-above-finger-sides -20) ; how far above the
                                         ; marshmallowy sides of the
                                         ; finger the marshmallowy
                                         ; sides of the thumb should
                                         ; be

(def gasket-sphere-fn 12) ; detail of sphere minkowski'd around edges.
                                        ; normally 20 or so?  severe
                                        ; performance impact. for me,
                                        ; with openscad 2015.03-2,
                                        ; this looked like big delays
                                        ; with lots of memory usage
                                        ; after the progress bar got
                                        ; to 1000.


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

(defn usb-cutout-place [shape]
    (->> shape
         (translate [0 (/ mount-height 2) 0])
         (translate [0 marshmallowy-sides-radius
                     (- marshmallowy-sides-radius)])
         (translate [0 0 (- marshmallowy-sides-downness)])
         #_(translate [0 0 -5]) ; for radius 12
         (key-place 2 0)))

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

                                        ; the around-edge vector is a
                                        ; list of the key places
                                        ; around the edge in
                                        ; order. this is a description
                                        ; of whether a given key place
                                        ; is around the edge.
(defn around-edge-p [row column]
  (and
   (finger-has-key-place-p row column)
   (or (= column (first columns))
       (= column (last columns))
       (= row (first rows))
       (= row (last rows))
       (not (finger-has-key-place-p row (inc column)))
       (not (finger-has-key-place-p (inc row) column))
       (not (finger-has-key-place-p (inc row) (dec column))))))

; This is the thing differenced out of the middle of the giant
; marshmallow to make room for the keys. It is much more complicated
; than just the hull of the key holes, because if it isn't concave,
; the marshmallowy sides will shrink away from the keys unevenly in
; places. It is a single definition, so that we can make hulls between
; thumb places and key places. This is necessary because the thumb
; tips away from the finger, so their separate prisms have a gorge
; between them.
(defn pyramid [distance-below shape]
  (let [below (->> shape
                   (scale [(/ (+ column-radius distance-below) column-radius)
                           (/ (+ row-radius distance-below) row-radius)
                           1])
                   (translate [0 0 (- distance-below)]))
        distance-above 0 ; taking the pyramid high enough above the
                         ; top to get rid of the detritus from a
                         ; hastily-formed keycap prism also takes away
                         ; the details of the marshmallowy sides that
                         ; the keycap prisms enable cutting out
                         ; neatly.
        above (->> shape
                   (scale [(/ (- column-radius distance-above) column-radius)
                           (/ (- row-radius distance-above) row-radius)
                           1])
                   (translate [0 0 distance-above]))]
    (hull above below)))

(defn frustum [distance-below narrow-percent place untent retent column row shape]
          (->> shape
               (scale [(/ (- 100 narrow-percent) 100)
                       (/ (- 100 narrow-percent) 100)
                       1])
               (place column row)
               untent
               (pyramid distance-below)
               retent))
(defn key-frustum [distance-below narrow-percent column row]
  (frustum distance-below narrow-percent
           key-place untent retent column row chosen-blank-single-plate))
(defn thumb-frustum [distance-below narrow-percent column row shape]
  (frustum distance-below narrow-percent
           thumb-place thumb-untent thumb-retent column row shape))
(defn silo [distance-above narrow-percent place widenings column row shape]
                                        ; see key-place
  (let [distance-below 6
        lower #(translate [0 0 (- distance-below)] %)
        shrink #(scale [(/ (- 100 narrow-percent) 100)
                        (/ (- 100 narrow-percent) 100)
                        1] %)
        widen #(let [[l r] (widenings column row)]
                 (hull (translate [(- (* mount-width l)) 0 0] %)
                       (translate [   (* mount-width r)  0 0] %)))
        extrude #(hull % (translate [0 0 (+ distance-above distance-below)] %))]
    (->> shape
         lower
         shrink
         widen
         extrude
         (place column row))))

(defn key-silo-widenings [col row]
  (let [bottom (fn [col]
                 (cond
                   (<= col (first columns)) [0 0]
                   (< col 2) [1 0]
                   (< col 3) [1 1]
                   (< col (last columns)) [0 1]
                   :else [0 0]))
        top (fn [col]
              (cond
                (<= col (first columns)) [0 1]
                (< col 2) [0 1]
                (< col 3) [0 0]
                (< col (last columns)) [1 0]
                :else [1 0]))
        linear (fn [col row]
                 (let [[bl br] (bottom col)
                       [tl tr] (top col)
                       row-amount (/ (- row (first rows)) (- (last rows) (first rows)))
                       left-widening (+ (* bl row-amount) (* tl (- 1 row-amount)))
                       right-widening (+ (* br row-amount) (* tr (- 1 row-amount)))]
                   [left-widening right-widening]))
        step (fn [col row]
               ; row numbers go up as you move from the top toward the thumb
               (if (some #{row} (drop (/ (count rows) 2) rows))
                 (bottom col)
                 (top col)))]
    (step col row)))


(defn finger-key-prism [distance-above narrow-percent]
  (let [ks #(silo distance-above narrow-percent key-place key-silo-widenings % %2 (sa-cap 1))
                                        ; we do all rows and columns,
                                        ; not knocking out those two
                                        ; keys, because this prism is
                                        ; for cutting out the top edge
                                        ; of the marshmallowy sides.
        row-split (/ (count rows) 2)
        top-rows (take row-split rows)
        bottom-rows (drop row-split rows)
        middle-two-rows (take-last 2 (take (inc row-split) rows))
        p4r (fn [rows] (for [c columns] (apply hull (for [r rows] (ks c r)))))
        top-prisms (p4r top-rows)
        middle-prisms (p4r middle-two-rows)
        bottom-prisms (p4r bottom-rows)]
    #_(apply union middle-prisms)(union
     (apply union (map vector top-prisms middle-prisms bottom-prisms)))))

(defn thumb-key-prism [distance-above narrow-percent]
  (let [ts #(silo distance-above narrow-percent thumb-place (fn [c r] [0 0]) % %2 %3)]
    (union (hull (ts 2 1 (sa-cap 1))
                 (ts 2 0 (sa-cap 1)))
           (hull (ts 2 0 (sa-cap 1))
                 (ts 2 -1 (sa-cap 1))
                 (ts 1 -1/2 (sa-cap 2)))
           (hull (ts 1 -1/2 (sa-cap 2))
                 (ts 0 -1/2 (sa-cap 2)))
           (hull (ts 2 1 (sa-cap 1))
                 (ts 1 1 (sa-cap 1)))
           (hull (ts 1 1 (sa-cap 1))
                 (ts 0 1 (sa-cap 1))))))


(defn thumb-prism [distance-below narrow-percent]
  (let [tfc #(thumb-frustum distance-below narrow-percent % %2 %3)]
    (union
                                        ; the key at thumb-place 1,1
                                        ; is not in this keyboard, but
                                        ; to make the outline be
                                        ; shaped right we need its
                                        ; frustum in our union
     (hull (tfc 1 1 chosen-blank-single-plate)
           (tfc 2 1 chosen-blank-single-plate)
           (tfc 2 0 chosen-blank-single-plate))
     (hull (tfc 1 1 chosen-blank-single-plate)
           (tfc 1 -1/2 double-plates-blank))
                                        ; yknow what, we're going to
                                        ; do the same thing with 0,1
     (hull (tfc 0 1 chosen-blank-single-plate)
           (tfc 1 1 chosen-blank-single-plate)
           (tfc 1 0 chosen-blank-single-plate))
     (hull (tfc 2 1 chosen-blank-single-plate)
           (tfc 2 0 chosen-blank-single-plate))
     (hull (tfc 2 0    chosen-blank-single-plate)
           (tfc 2 -1   chosen-blank-single-plate)
           (tfc 0 -1/2 double-plates-blank))
     (hull (tfc 0 -1/2 double-plates-blank)
           (tfc 1 -1/2 double-plates-blank)))))

(defn finger-edge-prism [distance-below narrow-percent]
  (apply union
                                        ; hah! around the edge, take
                                        ; successive pairs of key
                                        ; frusta, and hull them
                                        ; together.
         (for [[[column1 row1] [column2 row2]]
               (map vector around-edge around-edge-rot1)]
           (hull (key-frustum distance-below narrow-percent column1 row1)
                 (key-frustum distance-below narrow-percent column2 row2)))))
                                        ; hull from the edge
                                        ; diagonally in one key, one
                                        ; way...
(defn finger-edge-zig-in-1-prism [distance-below narrow-percent]
  (apply union
         (for [column (drop-last columns)
               row (drop-last rows)
               :when (and (finger-has-key-place-p row column)
                          (finger-has-key-place-p (inc row) (inc column))
                          (or (not (around-edge-p row column))
                              (not (around-edge-p (inc row) (inc column))))
                          (not (and (not (around-edge-p row column))
                                    (not (around-edge-p (inc row) (inc column))))))]
           (hull (key-frustum distance-below narrow-percent column row)
                 (key-frustum distance-below narrow-percent (inc column) (inc row))))))
                                        ; ... then the other.
(defn finger-edge-zag-in-1-prism [distance-below narrow-percent]
  (apply union
         (for [column (drop 1 columns)
               row (drop-last rows)
               :when (and (finger-has-key-place-p row column)
                          (finger-has-key-place-p (inc row) (dec column))
                          (or (not (around-edge-p row column))
                              (not (around-edge-p (inc row) (dec column))))
                          (not (and (not (around-edge-p row column))
                                    (not (around-edge-p (inc row) (dec column))))))]
           (hull (key-frustum distance-below narrow-percent column row)
                 (key-frustum distance-below narrow-percent (dec column) (inc row))))))
(defn finger-middle-blob-prism [distance-below narrow-percent]
  (apply hull
         (for [column (drop-last (drop 1 columns))
               row (drop-last (drop 1 rows))
               :when (and (finger-has-key-place-p row column)
                          (finger-has-key-place-p (inc row) column)
                          (finger-has-key-place-p row (inc column)))]
           (key-frustum distance-below narrow-percent column row))))

(defn finger-prism [distance-below narrow-percent]
  (apply union (for [prism
                     [finger-edge-prism
                      finger-edge-zig-in-1-prism
                      finger-edge-zag-in-1-prism
                      finger-middle-blob-prism]]
                 (prism distance-below narrow-percent))))


(defn finger-case-bottom-sphere [flatness downness]
  "flatness ill-understood; 0-120 seems valid. downness is how far down the thing is from the top case."
  (let [sph-row-radius (+ flatness row-radius)
        sph-column-radius (+ flatness column-radius)
        key-sphere (->> (sphere sph-row-radius)
                                        ; i don't see why this ratio
                                        ; is scaled by half
                        (scale [(/ (/ sph-column-radius sph-row-radius) 2) 1 1]))
        left-of (let [big 600] (translate [(- (* 1/2 big)) 0 0] (cube big big big)))
        left-of-col-4 (key-place 4 2 (translate [(- (* 1/2 mount-width)) 0 0] left-of))
        most-keys-sphere (intersection key-sphere left-of-col-4)
        other-keys-sphere (difference (translate [0 -5.8 5.64] key-sphere) left-of-col-4)
        fractured-key-sphere (union most-keys-sphere other-keys-sphere)]
                                      
                                 
                        
    (->> fractured-key-sphere
         (color [0 0.7 0.7 0.8])
                                        ; from key-place
         (translate [0 0 sph-row-radius])
                                        ; tenting is pi over 12; outer
                                        ; rows raised so rotate a little less
         (rotate (* (/ π 12) 0.85) [0 1 0])
         (translate [0 0 (- sph-row-radius)])
         (translate [0 0 row-radius])
         (translate [0 0 (- flatness downness)]))))

(defn thumb-case-bottom-sphere [flatness downness]
  (let [sph-row-radius (+ flatness row-radius)
        sph-column-radius (+ flatness column-radius)]
    (->> (sphere sph-row-radius)
         (scale [(/ (/ sph-column-radius sph-row-radius) 2) 1 1])
         (color [0.7 0 0.7 0.8])
                                        ; from thumb-place
         (translate [mount-width 0 0])
         (translate [0 0 sph-row-radius])
         (rotate (* π (- 1/4 3/16)) [0 0 1])
         ; perhaps this should be tenting-angle, but look at the
         ; axis. that will change what the translation after it
         ; needs to be when you change tenting-angle.
         (rotate (* (/ π 12) 0.9) [1 1 0])
         (translate [-53 -45 40])
         (translate [0 0 (- sph-row-radius)])
         (translate [0 0 row-radius])
         (translate [0 0 (- flatness downness)]))))

(def distance-below-to-intersect (max (+ marshmallowy-sides-downness
                                         marshmallowy-sides-flatness) 35))
                                        ; the thumb is set above (+z)
                                        ; the finger, but its prism
                                        ; interacts with the
                                        ; marshmallowy sides of the
                                        ; finger. so its prism needs
                                        ; to be taller.
(def thumb-distance-below (* 1.5 distance-below-to-intersect))

(def case-bottom-shell
  ; note these spheres are down farther than the ones used for the ribbon
  (let [finger-sphere (finger-case-bottom-sphere
                       marshmallowy-sides-flatness
                       (+ marshmallowy-sides-downness
                          marshmallowy-sides-radius))
        thumb-sphere (thumb-case-bottom-sphere
                      marshmallowy-sides-flatness
                      (+ marshmallowy-sides-downness
                         marshmallowy-sides-radius
                         (- thumb-sides-above-finger-sides)))
        bottom-spheres (union finger-sphere thumb-sphere)
        the-shell (difference bottom-spheres
                              (translate [0 0 marshmallowy-sides-thickness]
                                         bottom-spheres))
        big-intersection-shape (union
                                (finger-prism distance-below-to-intersect 0)
                                (thumb-prism thumb-distance-below 0))]
    (intersection the-shell big-intersection-shape)))

(defn key-placed-outline [down out]
  (let [nby (+ (* 1/2 mount-height) out)
        sby (- nby)
        eby (+ (* 1/2 mount-width) out)
        wby (- eby)
        n (translate [0 nby (- down)] web-post)
        s (translate [0 sby (- down)] web-post)
        e (translate [eby 0 (- down)] web-post)
        w (translate [wby 0 (- down)] web-post)
        sw (translate [wby sby (- down)] web-post)
        se (translate [eby sby (- down)] web-post)
        ne (translate [eby nby (- down)] web-post)
        nw (translate [wby nby (- down)] web-post)
        kp key-place
        tp thumb-place
        places [(kp -1 2 sw)
                (kp -1 2 w)
                (kp -1 1 w)
                (kp -1 0 w)
                (kp -1 0 nw)
                (kp -1 0 n)
                (kp 0 0 n)
                (kp 1 0 n)
                (kp 2 0 n)
                (kp 3 0 n)
                (kp 4 0 n)
                (kp 5 0 n)
                (kp 5 0 ne)
                (kp 5 0 e)
                (kp 5 1 e)
                (kp 5 2 e)
                (kp 5 3 e)
                (kp 5 4 e)
                (kp 5 4 se)
                (kp 5 4 s)
                (kp 4 4 s)
                (kp 3 4 s)
                (kp 2 4 s)
                (kp 1 4 s)
                (tp 0 -1 se)
                (tp 0 -1 s)
                (tp 1 -1 s)
                (tp 2 -1 s)
                (tp 2 -1 sw)
                (tp 2 -1 w)
                (tp 2 0 w)
                (tp 2 1 w)
                (tp 2 1 nw)
                (tp 2 1 n)]
        thisnext (map vector places (concat (rest places) [(first places)]))
        ribbon (apply union
                      (for [[this next] thisnext]
                        (hull this next)))]
    ribbon))

(defn big-marshmallowy-sides [flatness downness thickness radius]
  (let [outline-thickness 1
        finger-sphere (finger-case-bottom-sphere
                       marshmallowy-sides-flatness
                       marshmallowy-sides-downness)
        thumb-sphere (thumb-case-bottom-sphere
                      marshmallowy-sides-flatness
                      (- marshmallowy-sides-downness
                         thumb-sides-above-finger-sides))
        finger-shell (difference finger-sphere (translate [0 0 thickness] finger-sphere))
        finger-thick-shell (difference (translate [0 0 (- thickness)] finger-sphere)
                                       (translate [0 0 (* 2 thickness)] finger-sphere))
        thumb-shell (difference thumb-sphere (translate [0 0 thickness] thumb-sphere))
        thumb-thick-shell (difference (translate [0 0 (- thickness)] thumb-sphere)
                                      (translate [0 0 (* 2 thickness)] thumb-sphere))
        finger-big-intersection-shape
        (finger-prism distance-below-to-intersect 0)
        finger-little-intersection-shape
        (finger-prism distance-below-to-intersect 2)
        thumb-big-intersection-shape
        (thumb-prism thumb-distance-below -5)
        thumb-little-intersection-shape
        (thumb-prism thumb-distance-below -3)
        finger-case-outline
        (difference (intersection finger-shell
                                  finger-big-intersection-shape)
                    (intersection finger-thick-shell
                                  finger-little-intersection-shape))
        thumb-case-outline
        (difference (intersection thumb-shell
                                  thumb-big-intersection-shape)
                    (intersection thumb-thick-shell
                                  thumb-little-intersection-shape))
        ;; the-outline (union (difference finger-case-outline
                                       ;; thumb-big-intersection-shape)
                           ;; (difference thumb-case-outline
                                       ;; finger-big-intersection-shape))
        the-outline (key-placed-outline (* 1/2 radius) 0)
        marshmallow-gasket (fn [r] (minkowski
                                    the-outline
                                    (binding [*fn* gasket-sphere-fn]
                                      (sphere r))))
        ; this -5 sets how far away from the keys the top of the
        ; marshmallowy sides will be.
        key-prism (union
                   (thumb-key-prism thumb-distance-below -5)
                   (finger-key-prism distance-below-to-intersect -5))
        sides (difference
               (difference (marshmallow-gasket radius)
                           (marshmallow-gasket (- radius thickness)))
               finger-big-intersection-shape
               thumb-big-intersection-shape
                                        ; the key at 0,4 is not part
                                        ; of the keyboard, but is
                                        ; necessary to remove a tab of
                                        ; the marshmallowy
                                        ; side. rather than making it
                                        ; part of around-edge, i've
                                        ; manually written it here
               (hull (key-frustum 30 0 0 4)
                     (key-frustum 30 0 1 4))
               (hull (key-frustum 30 0 0 4)
                     (key-frustum 30 0 0 3))
               (hull (key-frustum 30 0 0 4)
                     (key-frustum 30 0 1 3))
               key-prism)]
    sides))

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

(def mallowy-sides-with-right-ports
  (difference
   (binding [*fn* 12]
     (union
      (big-marshmallowy-sides marshmallowy-sides-flatness
                              marshmallowy-sides-downness
                              marshmallowy-sides-thickness
                              marshmallowy-sides-radius)
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

(spit "things/dactyl-blank-all.scad"
      (write-scad
       (union
        (apply union mallow-slices-right)
        #_(union
         (finger-case-bottom-sphere marshmallowy-sides-flatness marshmallowy-sides-downness)
         (thumb-case-bottom-sphere marshmallowy-sides-flatness marshmallowy-sides-downness))
        
        #_(union
         (apply union key-blanks-pieces)
         thumb-blanks)
        (union caps thumbcaps)
        #_(union
         (thumb-key-prism 30 -5)
         (finger-key-prism 30 -5))
        #_(color [0 1 0 0.7] (finger-prism 30 0))
        #_(color [0 1 0 0.7] (thumb-top-outline-prism2 45 0))
        #_(union dactyl-top-right-thumb
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
