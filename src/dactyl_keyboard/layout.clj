(ns dactyl-keyboard.layout
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [dactyl-keyboard.switch-hole :refer :all]
            [dactyl-keyboard.keycaps :refer [sa-cap]]
            [dactyl-keyboard.placement :refer [key-place thumb-place]]
            [unicode-math.core :refer :all]))

(def layouts
  {:y-and-b
; we split the keyholes into pieces for smaller print volume and quicker printing
   {:columns-pieces [(range -1 0) (range 0 2) (range 2 4) (range 4 6)]
    :rows (range 0 5)
    :finger-knockouts [[0 4] [-1 4]]
    :around-edge [[-1 3] [-1 2] [-1 1] [-1 0]
                  [0 0] [1 0] [2 0] [3 0] [4 0] [5 0]
                  [5 1] [5 2] [5 3] [5 4]
                  [4 4] [3 4] [2 4] [1 4]
                  [1 3] [0 3]]
    :thumb-glue-joint-left-of [[-1 3] [1 4]]}

   :mini
   {:columns-pieces [(range 2 4)]
    :rows (range 0 2)
    :finger-knockouts []
    :around-edge [[:sw-corner 2 2] [:w-edge 2 1] [:nw-corner 2 0]
                  [:n-edge 3 0] [:ne-corner 4 0]
                  [:e-edge 4 1] [:se-corner 4 2]
                  [:s-edge 3 2]]}})


(def chosen-layout (layouts :mini))


(def columns-pieces (chosen-layout :columns-pieces))
(def columns (apply concat columns-pieces))
(def rows (chosen-layout :rows))

                                        ; this defines the keys
                                        ; missing from the finger part
                                        ; that make room for the thumb
(defn finger-has-key-place-p [row column]
  (or (for [[c r] (chosen-layout :finger-knockouts)]
        (and (= c column) (= r row)))))

                                        ; coordinates of keys around
                                        ; the edge of the finger
                                        ;               >*****v
                                        ;               *.....*
                                        ;               *.....*
                                        ; start here -> ^**...*
                                        ;               xx****<
(def around-edge (chosen-layout :around-edge))


(defn rot1 [s] (concat (rest s) (list (first s))))
(def around-edge-rot1 (rot1 around-edge))

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
       ; this may not be general to any possible set of knockouts
       (not (finger-has-key-place-p row (inc column)))
       (not (finger-has-key-place-p (inc row) column))
       (not (finger-has-key-place-p (inc row) (dec column))))))


(defn thumb-glue-joint-left-of-p [row column]
  (or (for [[c r] (chosen-layout :thumb-glue-joint-left-of)]
        (and (= c column) (= row r)))))


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

(def thumbcaps
  (union
   (thumb-2x-column (sa-cap 2))
   (thumb-place 1 -1/2 (sa-cap 2))
   #_(thumb-place 1 1 (sa-cap 1))
   (thumb-1x-column (sa-cap 1))))
