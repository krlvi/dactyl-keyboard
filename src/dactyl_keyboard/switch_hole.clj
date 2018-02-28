(ns dactyl-keyboard.switch-hole
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
(def sa-length 18.25)
(def sa-double-length 37.5)

(def plate-thickness 4)
(def web-thickness 3.5)

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



                                        ; thumb plates

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
