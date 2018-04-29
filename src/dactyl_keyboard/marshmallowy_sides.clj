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




(defn finger-key-prism [distance-above narrow-percent]
  (let [ks #(silo distance-above narrow-percent key-place key-silo-widenings % %2 (sa-cap 1))
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


(def around-edge-region
                                        ; from [col row] to the items
                                        ; before, at the place, and
                                        ; after in around-edge
  (let [places around-edge]
    (apply merge
           (for [[one two three]
                 (map vector
                      places
                      (concat (drop 1 places) [(first places)])
                      (concat (drop 2 places) [(first places)
                                               (second places)]))]
             {[(nth two 1) (nth two 2)] [one two three]}))))

(defn key-placed-outline [notation down out shape closed]
  (let [nby (+ (* 1/2 mount-height) out)
        sby (- nby)
        eby (+ (* 1/2 mount-width) out)
        wby (- eby)
        n (translate [0 nby (- down)] shape)
        s (translate [0 sby (- down)] shape)
        e (translate [eby 0 (- down)] shape)
        w (translate [wby 0 (- down)] shape)
        sw (translate [wby sby (- down)] shape)
        se (translate [eby sby (- down)] shape)
        ne (translate [eby nby (- down)] shape)
        nw (translate [wby nby (- down)] shape)
        places {:k key-place :t thumb-place}
        places
        (apply concat
               (for [[g p c r] notation]
                 (let [pf (places p)]
                   (cond
                     (= g :nw) [(pf c r w) (pf c r nw) (pf c r n)]
                     (= g :n)    [(pf c r n)]
                     (= g :ne) [(pf c r n) (pf c r ne) (pf c r e)]
                     (= g :e)    [(pf c r e)]
                     (= g :se) [(pf c r e) (pf c r se) (pf c r s)]
                     (= g :s)    [(pf c r s)]
                     (= g :sw) [(pf c r s) (pf c r sw) (pf c r w)]
                     (= g :w)    [(pf c r w)]
                     (= g :sw-in) [(pf c r sw)]
                     (= g :se-in) [(pf c r se)]
                     (= g :nw-in) [(pf c r nw)]
                     (= g :ne-in) [(pf c r ne)]))))
        thisnext (if closed
                   (map vector places (concat (rest places) [(first places)]))
                   (map vector places (rest places)))
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
        ;; finger-case-outline
        ;; (difference (intersection finger-shell
                                  ;; finger-big-intersection-shape)
                    ;; (intersection finger-thick-shell
                                  ;; finger-little-intersection-shape))
        ;; thumb-case-outline
        ;; (difference (intersection thumb-shell
                                  ;; thumb-big-intersection-shape)
                    ;; (intersection thumb-thick-shell
                                  ;; thumb-little-intersection-shape))
        ;; the-outline (union (difference finger-case-outline
                                       ;; thumb-big-intersection-shape)
                           ;; (difference thumb-case-outline
                                       ;; finger-big-intersection-shape))
        the-outline (key-placed-outline around-edge (* 1/2 radius) 0 web-post true)
        marshmallow-gasket (fn [r] (key-placed-outline around-edge (* 1/2 radius) 0 (with-fn gasket-sphere-fn (sphere r)) true))
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
    (union sides)))

(def mallowy-sides-right
  (big-marshmallowy-sides marshmallowy-sides-flatness
                          marshmallowy-sides-downness
                          marshmallowy-sides-thickness
                          marshmallowy-sides-radius))
