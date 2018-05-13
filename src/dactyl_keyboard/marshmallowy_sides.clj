(ns dactyl-keyboard.marshmallowy-sides
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

(def gasket-sphere-fn 9) ; detail of sphere minkowski'd around edges.
                                        ; normally 20 or so?  severe
                                        ; performance impact. for me,
                                        ; with openscad 2015.03-2,
                                        ; this looked like big delays
                                        ; with lots of memory usage
                                        ; after the progress bar got
                                        ; to 1000.


(def distance-below-to-intersect (max (+ marshmallowy-sides-downness
                                         marshmallowy-sides-flatness) 35))
                                        ; the thumb is set above (+z)
                                        ; the finger, but its prism
                                        ; interacts with the
                                        ; marshmallowy sides of the
                                        ; finger. so its prism needs
                                        ; to be taller.
(def thumb-distance-below (* 1.5 distance-below-to-intersect))


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

(defn frustum [distance-below narrow-percent placer shape]
          (->> shape
               (scale [(/ (- 100 narrow-percent) 100)
                       (/ (- 100 narrow-percent) 100)
                       1])
               (pyramid distance-below)
               placer))
(defn key-frustum [distance-below narrow-percent column row]
  (frustum distance-below narrow-percent
           (partial key-place column row) chosen-blank-single-plate))
(defn thumb-frustum [distance-below narrow-percent column row shape]
  (frustum distance-below narrow-percent
           (partial thumb-place column row) shape))


(defn silo [distance-below distance-above narrow-percent place widenings column row shape]
                                        ; see key-place
  (let [lower #(translate [0 0 (- distance-below)] %)
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



(defn finger-key-prism [distance-below distance-above narrow-percent]
  (let [ks #(silo distance-below distance-above narrow-percent key-place key-silo-widenings % %2 (sa-cap 1))
                                        ; we do all rows and columns,
                                        ; not knocking out those two
                                        ; keys, because this prism is
                                        ; for cutting out the top edge
                                        ; of the marshmallowy sides.
        row-split (quot (count rows) 2)
        top-rows (take row-split rows)
        bottom-rows (drop row-split rows)
        middle-two-rows (take-last 2 (take (inc row-split) rows))
        p4r (fn [rows] (for [c columns] (apply hull (for [r rows] (ks c r)))))
        top-prisms (p4r top-rows)
        middle-prisms (p4r middle-two-rows)
        bottom-prisms (p4r bottom-rows)]
    #_(apply union middle-prisms)(union
     (apply union (map vector top-prisms middle-prisms bottom-prisms)))))

(defn thumb-silo-widenings [c r] [0 0])

(defn thumb-key-prism [distance-below distance-above narrow-percent]
  (let [ts #(silo distance-below distance-above narrow-percent thumb-place thumb-silo-widenings % %2 %3)]
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
         (for [[[grav1 place1 column1 row1] [grav2 place2 column2 row2]]
               (map vector around-edge around-edge-rot1)
               :when (and (= place1 :k) (= place2 :k))]
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


(def around-edge-region
                                        ; a hashmap from a place [col
                                        ; row] to the items before, at
                                        ; the place, and after in
                                        ; around-edge
  (let [places around-edge]
    (apply merge
           (for [[before here after]
                 (map vector
                      places
                      (concat (drop 1 places) (take 1 places))
                      (concat (drop 2 places) (take 2 places)))]
             {(apply vector (drop 1 here)) [before here after]}))))

(defn key-placed-outline [notation down out shape closed]
  "Place copies of shape around the edge of the keyboard as given by
  notation, hulling them together pairwise. If closed, the last is
  hulled to the first. Down and out are how far downward (parallel to
  the axis of key depression) and outward from the edge to place the
  shapes."
  (let [placers (mapcat (partial sides-place-fns down out) notation)
        dots (map #(% shape) placers)
        thisnext (if closed
                   (map vector dots (concat (drop 1 dots) (take 1 dots)))
                   (map vector dots (drop 1 dots)))
        ribbon (apply union
                      (for [[this next] thisnext]
                        (hull this next)))]
    ribbon))

(defn marshmallowy-partial-sides [flatness downness thickness radius notation]
  "Produce only part of the marshmallowy sides. This is used to
  construct connectors between pieces of the sides, because
  intersecting the entire sides object with a thin cube to get a
  little rind of side to hull with something requires talking about
  the entire sides object multiple times, which is too slow. This
  function gets the top edge of the sides quite wrong, particularly
  where columns are staggered inward, but is fit for its original
  purpose."
  (let [outline-thickness 1
        bottom-remove
        (apply hull-pairs (for [n notation]
                       (frustum distance-below-to-intersect 0
                                (key-place-fn n) chosen-blank-single-plate)))
        top-remove
        (apply hull-pairs (for [n notation]
                       (let [[g p c r] n
                             place ({:k key-place :t thumb-place} p)
                             widenings ({:k key-silo-widenings
                                         :t thumb-silo-widenings} p)]
        ; this -5 sets how far away from the keys the top of the
        ; marshmallowy sides will be.
                             (silo distance-below-to-intersect 6 -5
                                   place widenings c r
                                   chosen-blank-single-plate))))
        marshmallow-gasket (fn [r] (key-placed-outline notation (* 1/2 radius) 0 (with-fn gasket-sphere-fn (sphere r)) false))
        tube (difference (marshmallow-gasket radius)
                         (marshmallow-gasket (- radius thickness)))
        sides (difference
               tube
               bottom-remove
               top-remove)]
    (union sides)))

(defn big-marshmallowy-sides [flatness downness thickness radius notation closed]
  (let [outline-thickness 1
        finger-big-intersection-shape
        (finger-prism distance-below-to-intersect 0)
        thumb-big-intersection-shape
        (thumb-prism thumb-distance-below -5)
        marshmallow-gasket (fn [r] (key-placed-outline notation (* 1/2 radius) 0 (with-fn gasket-sphere-fn (sphere r)) closed))
        ; this -5 sets how far away from the keys the top of the
                                        ; marshmallowy sides will be.
                                        ; 6 was orig written in silo
                                        ; definition. from there i
                                        ; don't kknow where it came
                                        ; from.
        key-prism (union
                   (thumb-key-prism 6 thumb-distance-below -5)
                   (finger-key-prism 6 distance-below-to-intersect -5))
        tube (difference (marshmallow-gasket radius)
                         (marshmallow-gasket (- radius thickness)))
        sides (difference
               tube
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
    (union sides)))

(def mallowy-sides-right
  (big-marshmallowy-sides marshmallowy-sides-flatness
                          marshmallowy-sides-downness
                          marshmallowy-sides-thickness
                          marshmallowy-sides-radius
                          around-edge true))





