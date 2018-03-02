(ns dactyl-keyboard.adafruit-usb-test
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.adafruit-usb :refer :all]))

(spit "things/adafruit-usb-test.scad"
      (write-scad
       (union
        (difference adafruit-usb-plate adafruit-usb-cutout))))
