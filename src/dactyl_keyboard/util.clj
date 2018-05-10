(ns dactyl-keyboard.util
  (:refer-clojure :exclude [use import])
  (:require [unicode-math.core :refer :all]
            [scad-clj.model :refer :all]))

(defn triangle-hulls [& shapes]
  (apply union
         (map (partial apply hull)
              (partition 3 1 shapes))))

(defn hull-pairs [& shapes]
  (apply union
         (for [[this next] (map vector shapes (drop 1 shapes))]
           (hull this next))))

(defn hull-a-grid [rows-of-columns]
  "Hull a grid of shapes together. If some of the shapes are nil, that is fine."
  (apply union
         (for [[row next-row] (map vector rows-of-columns
                                   (drop 1 rows-of-columns))]
           (for [[column next-column column-in-next-row
                  next-column-in-next-row]
                 (map vector row (drop 1 row) next-row (drop 1 next-row))]
             (union
              (hull column next-column column-in-next-row)
              (hull next-column next-column-in-next-row column-in-next-row))))))

(defn bottom [height p]
  (->> (project p)
       (extrude-linear {:height height :twist 0 :convexity 0})
       (translate [0 0 (/ height 2)])))

(defn bottom-hull [p]
  (hull p (bottom 1 p)))

; https://tauday.com
(def ^:const τ (* π 2))

; csg food
(def ^:const ε 0.001)

(def ^:const √2 (Math/sqrt 2))

