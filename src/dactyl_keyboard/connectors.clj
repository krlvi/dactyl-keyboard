;; --- BEGIN AGPLv3-preamble ---
;; Dactyl Marshmallow ergonomic keyboard generator
;; Copyright (C) 2015, 2018 Matthew Adereth and Jared Jennings
;;
;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU Affero General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Affero General Public License for more details.
;;
;; You should have received a copy of the GNU Affero General Public License
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.
;; --- END AGPLv3-preamble ---
(ns dactyl-keyboard.connectors
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [dactyl-keyboard.switch-hole :refer :all]
            [dactyl-keyboard.placement :refer :all]
            [dactyl-keyboard.layout :refer :all]
            [dactyl-keyboard.screw-hole :refer :all]
            [dactyl-keyboard.connectors-positions :refer :all]
            ))

;;;;;;;;;;;;;;;;;;;;
;; Web Connectors ;;
;;;;;;;;;;;;;;;;;;;;

#_(def web-post (->> (cube post-size post-size web-thickness)
                   (translate [0 0 (+ (/ web-thickness -2)
                                      plate-thickness)])))
(def web-post (call-module "WebPost"))
(def above-web-post (call-module "AboveWebPost"))

;; these are like web-posts, but lying down.  horizontal or
;; vertical (EW / NS key-place-wise); bottom or top (key actuation
;; axis wise); and which way it sticks out (north, south, east, west
;; key-place-wise).
(def web-log-hbw (call-module "WebLogHBW"))
(def web-log-hbe (call-module "WebLogHBE"))
(def web-log-htw (call-module "WebLogHTW"))
(def web-log-hte (call-module "WebLogHTE"))
(def web-log-vbn (call-module "WebLogVBN"))
(def web-log-vbs (call-module "WebLogVBS"))
(def web-log-vtn (call-module "WebLogVTN"))
(def web-log-vts (call-module "WebLogVTS"))


  ;; -.b..: logs lying at the bottom of the web
  ;;              -hb..: sticking off the east and west sides
  (def web-log-hbse (se web-log-hbe)) ;;  --[    ]--
  (def web-log-hbsw (sw web-log-hbw)) ;;    [    ]
  (def web-log-hbne (ne web-log-hbe)) ;;    [    ]
  (def web-log-hbnw (nw web-log-hbw)) ;;  --[    ]--
  ;;              -vb..: sticking off the north and south
  ;;                                        |    |
  (def web-log-vbnw (nw web-log-vbn)) ;;    ------
  (def web-log-vbsw (sw web-log-vbs)) ;;    |    |
  (def web-log-vbne (ne web-log-vbn)) ;;    |    |
  (def web-log-vbse (se web-log-vbs)) ;;    |    |
  (def web-log-vbs  (s  web-log-vbs)) ;;    ------
  ;;                                        | |  |
  ;; -.t..: logs floating at the top of the web
  ;; (same places around the key hole as above)
  (def web-log-htse (se web-log-hte))
  (def web-log-htsw (sw web-log-htw))
  (def web-log-htne (ne web-log-hte))
  (def web-log-htnw (nw web-log-htw))
  (def web-log-vtnw (nw web-log-vtn))
  (def web-log-vtsw (sw web-log-vts))
  (def web-log-vtne (ne web-log-vtn))
  (def web-log-vtse (se web-log-vts))

(defn row-connector [column row]
  (triangle-hulls
     (key-place (inc column) row (nw web-post))
     (key-place column row (ne web-post))
     (key-place (inc column) row (sw web-post))
     (key-place column row (se web-post))))

(defn row-connectors [column]
  (for [row rows
        :when (and (finger-has-key-place-p column row)
                   (finger-has-key-place-p (inc column) row))]
    (row-connector column row)))

(defn diagonal-connector [column row]
  (triangle-hulls
     (key-place column row (se web-post))
     (key-place column (inc row) (ne web-post))
     (key-place (inc column) row (sw web-post))
     (key-place (inc column) (inc row) (nw web-post))))
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
        :when (and (finger-has-key-place-p (inc column) (inc row))
                   (finger-has-key-place-p column (inc row)))]
    (diagonal-connector column row)))

(defn column-connector [column row]
  (triangle-hulls
     (key-place column row (sw web-post))
     (key-place column row (se web-post))
     (key-place column (inc row) (nw web-post))
     (key-place column (inc row) (ne web-post))))
(defn column-connector-untranslate [shape]
  (translate [0 (- (- (/ mount-height 2) post-adj)) 0] shape))
(defn column-connector-retranslate [shape]
  (translate [0 (- (/ mount-height 2) post-adj) 0] shape))

(defn column-connectors [column]
  (for [row (drop-last rows)
        :when (finger-has-key-place-p column (inc row))]
    (column-connector column row)))

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

(def screw-holes-in-fingerpieces-minus
  (for [columns columns-pieces]
    (apply union
           (for [place screw-holes-at]
             (let [[p c r z] place]
               (if (and (= p :k)
                        (>= c (first columns))
                        (<= c (last columns)))
                 (->> frame-screw-hole-minus
                      (color [1 0 0])
                      (translate [0 0 z])
                      ((key-place-fn place)))))))))

(def screw-holes-in-fingerpieces-plus
  (for [columns columns-pieces]
    (apply union
           (for [place screw-holes-at]
             (let [[p c r z] place]
               (if (and (= p :k)
                        (>= c (first columns))
                        (<= c (last columns)))
                 (->> frame-screw-hole-plus
                      (translate [0 0 z])
                      ((key-place-fn place)))))))))


(def screw-holes-in-thumb-plus
  (for [columns columns-pieces]
    (apply union
           (for [place screw-holes-at]
             (let [[p c r z] place]
               (if (= p :t)
                 (->> frame-screw-hole-plus
                      (translate [0 0 z])
                      ((key-place-fn place)))))))))

(def screw-holes-in-thumb-minus
  (for [columns columns-pieces]
    (apply union
           (for [place screw-holes-at]
             (let [[p c r z] place]
               (if (= p :t)
                 (->> frame-screw-hole-minus
                      (color [1 0 0])
                      (translate [0 0 z])
                      ((key-place-fn place)))))))))

(def thumb-connectors
  (union
   (apply union
          (concat
           (for [column [1 2] row [-1 0]]
             (triangle-hulls (thumb-place column row (se web-post))
                             (thumb-place column row (ne web-post))
                             (thumb-place (dec column) row (sw web-post))
                             (thumb-place (dec column) row (nw web-post))))
           (for [column [0 1 2] row [0]]
             (triangle-hulls
              (thumb-place column row (sw web-post))
              (thumb-place column row (se web-post))
              (thumb-place column (dec row) (nw web-post))
              (thumb-place column (dec row) (ne web-post))))
           (for [column [1 2] row [0]]
             (triangle-hulls
              (thumb-place column row (se web-post))
              (thumb-place (dec column) row (sw web-post))
              (thumb-place column (dec row) (ne web-post))
              (thumb-place (dec column) (dec row) (nw web-post))))))
   (let [
         plate-height (/ (- sa-double-length mount-height) 2)
         thumb-tl (->> (nw web-post)
                       (translate [0 plate-height 0]))
         thumb-bl (->> (sw web-post)
                       (translate [0 (- plate-height) 0]))
         thumb-tr (->> (ne web-post)
                       (translate [0 plate-height 0]))
         thumb-br (->> (se web-post)
                       (translate [0 (- plate-height) 0]))]
     (union

      ;;Connecting the thumb to everything
      (triangle-hulls
       (hull (key-place 2 4 (se web-post)) (key-place 2 4 web-log-vbse))
       (hull (thumb-place 0 -1 (se web-post)) (thumb-place 0 -1 web-log-hbse))
       (hull (key-place 2 4 (sw web-post)) (key-place 2 4 web-log-vbsw))
       (hull (thumb-place 0 -1 (ne web-post)) (thumb-place 0 -1 web-log-hbne))
       (hull (key-place 2 4 (sw web-post)) (key-place 2 4 web-log-vbsw))
       (hull (thumb-place 0 0 (se web-post)) (thumb-place 0 0 web-log-hbse))
       (hull (key-place 2 4 (sw web-post)) (key-place 2 4 web-log-hbsw))
       (thumb-place 0 0 (ne web-post)))

      (triangle-hulls
       (key-place 2 3 (sw web-post))
       (key-place 2 4 (nw web-post))
       (key-place 1 3 (se web-post)))
      (triangle-hulls
       (thumb-place 0 0 (n web-post))
       (hull (key-place 1 3 (s web-post)) (key-place 1 3 web-log-vbs))
       (hull (thumb-place 0 0 (nw web-post)) (thumb-place 0 0 web-log-vtnw))
       (hull (key-place 1 3 (sw web-post)) (key-place 1 3 web-log-vbsw))
       (hull (thumb-place 1 0 (ne web-post)) (thumb-place 1 0 web-log-vtne))
       (hull (key-place 0 3 (sw web-post)) (key-place 0 3 web-log-vbsw))
       (hull (key-place 0 3 (sw web-post)) (key-place 0 3 web-log-vbsw))
       (hull (thumb-place 1 0 (ne web-post)) (thumb-place 1 0 web-log-vtne))
       (hull (thumb-place 1 0 (ne web-post)) (thumb-place 1 0 web-log-vtne))
       (thumb-place 1 0 (nw web-post)))))))

