(ns dactyl-keyboard.adafruit-usb
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [unicode-math.core :refer :all]))

                                        ; https://www.cs.jhu.edu/~carlson/download/datasheets/Micro-USB_1_01.pdf
                                        ; Micro USB Cables and
                                        ; Connectors Specification
                                        ; 1.01 Figure 4-5, Page 18.
                                        ; An overmold is the plastic
                                        ; that goes around the metal
                                        ; and wires of a usb
                                        ; connector. The standard
                                        ; overmold size is very
                                        ; chunky, and every actual
                                        ; cable should be smaller.
(def micro-b-overmold-width 11)
(def micro-b-overmold-height 9)

                                        ; Adafruit Panel Mount Extension USB Cable - Micro B Male to Micro B Female [ADA3258]
                                        ; "4-40 screws... 18mm apart"
                                        ; 4-40 -> 0.112" major diameter
(def adafruit-usb-screws-diameter 2.5)
(def adafruit-usb-screws-center 18)

(def adafruit-usb-plate-thickness 3)
(def adafruit-usb-cutout-depth 15)

(def adafruit-usb-cutout
  (let [overmold-hole (cube micro-b-overmold-width
                            adafruit-usb-cutout-depth
                            micro-b-overmold-height)
        screw-hole-1 (->>
                      (cylinder (/ adafruit-usb-screws-diameter 2)
                                adafruit-usb-cutout-depth)
                      (rotate (/ π 2) [1 0 0])
                      (translate [(/ adafruit-usb-screws-center 2)
                                  0 0]))
        screw-hole-2 (mirror [-1 0 0] screw-hole-1)]
    (union overmold-hole screw-hole-1 screw-hole-2)))

(def adafruit-usb-plate
  (let [thickness adafruit-usb-plate-thickness
        r (* 4 (* 1/2 adafruit-usb-screws-diameter))
        screw-plate-1 (->>
                       (cylinder r thickness)
                       (rotate (* 1/4 τ) [1 0 0])
                       (translate
                        [(* 1/2 adafruit-usb-screws-diameter) 0 0]))
        screw-plate-2 (mirror [-1 0 0] screw-plate-1)]
    (minkowski screw-plate-1 screw-plate-2)))
