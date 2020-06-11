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
            [unicode-math.core :refer :all]))

;;;;;;;;;;;;;;;;;;;;
;; Web Connectors ;;
;;;;;;;;;;;;;;;;;;;;

; web-thickness defined in switch-hole
(def post-size 0.1)
#_(def web-post (->> (cube post-size post-size web-thickness)
                   (translate [0 0 (+ (/ web-thickness -2)
                                      plate-thickness)])))
(def web-post (call-module "WebPost"))
;; these are like web-posts, but lying down.  horizontal or
;; vertical (key-place-wise); bottom or top (key actuation axis wise);
;; left, right, top and bottom (key-place-wise, as with web-posts)
(def web-log-hbw (call-module "WebLogHBW"))
(def web-log-hbe (call-module "WebLogHBE"))
(def web-log-htw (call-module "WebLogHTW"))
(def web-log-hte (call-module "WebLogHTE"))
(def web-log-vbn (call-module "WebLogVBN"))
(def web-log-vbs (call-module "WebLogVBS"))
(def web-log-vtn (call-module "WebLogVTN"))
(def web-log-vts (call-module "WebLogVTS"))


(def post-adj (/ post-size 2))
(let [se #(translate [(- (/ mount-width 2) post-adj)
                      (+ (/ mount-height -2) post-adj) 0] %)
      sw #(translate [(+ (/ mount-width -2) post-adj)
                      (+ (/ mount-height -2) post-adj) 0] %)
      ne #(translate [(- (/ mount-width 2) post-adj)
                      (- (/ mount-height 2) post-adj) 0] %)
      nw #(translate [(+ (/ mount-width -2) post-adj)
                      (- (/ mount-height 2) post-adj) 0] %)
      n #(translate [0 (- (/ mount-height 2) post-adj) 0] %)
      s #(translate [0 (+ (/ mount-height -2) post-adj) 0] %)
      w #(translate [(+ (/ mount-width -2) post-adj) 0 0] %)
      e #(translate [(- (/ mount-width 2) post-adj) 0 0] %)]
  (def web-post-tr (ne web-post))
  (def web-post-tl (nw web-post))
  (def web-post-bl (sw web-post))
  (def web-post-br (se web-post))
  (def web-post-ne (ne web-post))
  (def web-post-nw (nw web-post))
  (def web-post-sw (sw web-post))
  (def web-post-se (se web-post))
  (def web-post-t (n web-post))
  (def web-post-b (s web-post))
  (def web-post-l (w web-post))
  (def web-post-r (e web-post))
  (def web-post-n (n web-post))
  (def web-post-s (s web-post))
  (def web-post-w (w web-post))
  (def web-post-e (e web-post))
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
  ;;                                        ------
  ;;                                        |    |
  ;; -.t..: logs floating at the top of the web
  ;; (same places around the key hole as above)
  (def web-log-htse (se web-log-hte))
  (def web-log-htsw (sw web-log-htw))
  (def web-log-htne (ne web-log-hte))
  (def web-log-htnw (nw web-log-htw))
  (def web-log-vtnw (nw web-log-vtn))
  (def web-log-vtsw (sw web-log-vts))
  (def web-log-vtne (ne web-log-vtn))
  (def web-log-vtse (se web-log-vts)))

(defn row-connector [column row]
  (triangle-hulls
     (key-place (inc column) row web-post-tl)
     (key-place column row web-post-tr)
     (key-place (inc column) row web-post-bl)
     (key-place column row web-post-br)))

(defn row-connectors [column]
  (for [row rows
        :when (finger-has-key-place-p column row)]
    (row-connector column row)))

(defn diagonal-connector [column row]
  (triangle-hulls
     (key-place column row web-post-br)
     (key-place column (inc row) web-post-tr)
     (key-place (inc column) row web-post-bl)
     (key-place (inc column) (inc row) web-post-tl)))
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
     (key-place column row web-post-bl)
     (key-place column row web-post-br)
     (key-place column (inc row) web-post-tl)
     (key-place column (inc row) web-post-tr)))
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

(def screw-holes-in-fingerpieces
  (for [columns columns-pieces]
    (apply union
           (for [place screw-holes-at]
             (let [[p c r] place]
               (if (and (= p :k)
                        (>= c (first columns))
                        (<= c (last columns)))
                 (->> frame-screw-hole
                      (color [1 0 0])
                      ((key-place-fn place)))))))))

(def screw-holes-in-thumb
  (for [columns columns-pieces]
    (apply union
           (for [place screw-holes-at]
             (let [[p c r] place]
               (if (= p :t)
                 (->> frame-screw-hole
                      (color [1 0 0])
                      ((key-place-fn place)))))))))

(def thumb-connectors
  (union
   (apply union
          (concat
           (for [column [1 2] row [-1 0]]
             (triangle-hulls (thumb-place column row web-post-br)
                             (thumb-place column row web-post-tr)
                             (thumb-place (dec column) row web-post-bl)
                             (thumb-place (dec column) row web-post-tl)))
           (for [column [0 1 2] row [0]]
             (triangle-hulls
              (thumb-place column row web-post-bl)
              (thumb-place column row web-post-br)
              (thumb-place column (dec row) web-post-tl)
              (thumb-place column (dec row) web-post-tr)))
           (for [column [1 2] row [0]]
             (triangle-hulls
              (thumb-place column row web-post-br)
              (thumb-place (dec column) row web-post-bl)
              (thumb-place column (dec row) web-post-tr)
              (thumb-place (dec column) (dec row) web-post-tl)))))
   (let [
         plate-height (/ (- sa-double-length mount-height) 2)
         thumb-tl (->> web-post-tl
                       (translate [0 plate-height 0]))
         thumb-bl (->> web-post-bl
                       (translate [0 (- plate-height) 0]))
         thumb-tr (->> web-post-tr
                       (translate [0 plate-height 0]))
         thumb-br (->> web-post-br
                       (translate [0 (- plate-height) 0]))]
     (union

      ;;Connecting the thumb to everything
      (triangle-hulls
       (hull (key-place 2 4 web-post-se) (key-place 2 4 web-log-vbse))
       (hull (thumb-place 0 -1 web-post-se) (thumb-place 0 -1 web-log-hbse))
       (hull (key-place 2 4 web-post-sw) (key-place 2 4 web-log-vbsw))
       (hull (thumb-place 0 -1 web-post-ne) (thumb-place 0 -1 web-log-hbne))
       (hull (key-place 2 4 web-post-sw) (key-place 2 4 web-log-vbsw))
       (hull (thumb-place 0 0 web-post-se) (thumb-place 0 0 web-log-hbse))
       (hull (key-place 2 4 web-post-sw) (key-place 2 4 web-log-hbsw))
       (thumb-place 0 0 web-post-ne))

      (triangle-hulls
       (key-place 2 3 web-post-sw)
       (key-place 2 4 web-post-nw)
       (key-place 1 3 web-post-se))
      (triangle-hulls
       (key-place 2 3 web-post-sw)
       (thumb-place 0 0 web-post-ne)
       (hull (key-place 1 3 web-post-sw) (key-place 1 3 web-log-vbsw))
       (hull (thumb-place 0 0 web-post-nw) (thumb-place 0 0 web-log-vtnw))
       (hull (key-place 0 3 web-post-se) (key-place 0 3 web-log-vbse))
       (hull (thumb-place 1 0 web-post-ne) (thumb-place 1 0 web-log-vtne))
       (hull (key-place 0 3 web-post-sw) (key-place 0 3 web-log-vbsw))
       (hull (key-place 0 3 web-post-sw) (key-place 0 3 web-log-vbsw))
       (hull (thumb-place 1 0 web-post-ne) (thumb-place 1 0 web-log-vtne))
       (hull (thumb-place 1 0 web-post-ne) (thumb-place 1 0 web-log-vtne))
       (thumb-place 1 0 web-post-nw))))))

