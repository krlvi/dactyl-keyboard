(ns dactyl-keyboard.util
  (:refer-clojure :exclude [use import])
  (:require [unicode-math.core :refer :all]
            [scad-clj.model :refer :all]))

(defn triangle-hulls [& shapes]
  (apply union
         (map (partial apply hull)
              (partition 3 1 shapes))))

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

