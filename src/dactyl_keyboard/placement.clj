(ns dactyl-keyboard.placement
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [dactyl-keyboard.switch-hole :refer :all]
            [dactyl-keyboard.keycaps :refer :all]
            [unicode-math.core :refer :all]))


;;;;;;;;;;;;;;;;;;;;;;;;;
;; Placement Functions ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

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

#_(defn key-place [column row shape]
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
         (translate [0 0 45]))))

(defn key-place [column row shape]
  (call-module-with-block "KeyPlace" column row shape))


(defn untent [shape] (rotate (- tenting-angle) [0 1 0] shape))
(defn retent [shape] (rotate tenting-angle [0 1 0] shape))


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
         (translate [0 0 45]))))



#_(defn thumb-place [column row shape]
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
         (translate [-53 -45 72]))))

(defn thumb-place [column row shape]
  (call-module-with-block "ThumbPlace" column row shape))

(defn thumb-untent [shape]
  (->> shape
       (translate [53 45 -40])
       (rotate (- (/ π 12)) [1 1 0])))
(defn thumb-retent [shape]
  (->> shape
       (rotate (/ π 12) [1 1 0])
       (translate [-53 -45 40])))
