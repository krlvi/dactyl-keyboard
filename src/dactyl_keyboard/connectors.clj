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
(def web-log-hbl (call-module "WebLogHBL"))
(def web-log-hbr (call-module "WebLogHBR"))
(def web-log-htl (call-module "WebLogHTL"))
(def web-log-htr (call-module "WebLogHTR"))


(def post-adj (/ post-size 2))
(let [br #(translate [(- (/ mount-width 2) post-adj)
                      (+ (/ mount-height -2) post-adj) 0] %)
      bl #(translate [(+ (/ mount-width -2) post-adj)
                      (+ (/ mount-height -2) post-adj) 0] %)
      tr #(translate [(- (/ mount-width 2) post-adj)
                      (- (/ mount-height 2) post-adj) 0] %)
      tl #(translate [(+ (/ mount-width -2) post-adj)
                      (- (/ mount-height 2) post-adj) 0] %)
      t #(translate [0 (- (/ mount-height 2) post-adj) 0] %)
      b #(translate [0 (+ (/ mount-height -2) post-adj) 0] %)
      l #(translate [(+ (/ mount-width -2) post-adj) 0 0] %)
      r #(translate [(- (/ mount-width 2) post-adj) 0 0] %)]
  (def web-post-tr (tr web-post))
  (def web-post-tl (tl web-post))
  (def web-post-bl (bl web-post))
  (def web-post-br (br web-post))
  (def web-post-t (t web-post))
  (def web-post-b (b web-post))
  (def web-post-l (l web-post))
  (def web-post-r (r web-post))
  ;; logs lying at the bottom of the web
  (def web-log-hbbr (br web-log-hbr))
  (def web-log-hbbl (bl web-log-hbl))
  (def web-log-hbtr (tr web-log-hbr))
  (def web-log-hbtl (tl web-log-hbl))
  ;; logs floating at the top of the web
  (def web-log-htbr (br web-log-htr))
  (def web-log-htbl (bl web-log-htl))
  (def web-log-httr (tr web-log-htr))
  (def web-log-httl (tl web-log-htl)))

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
        :when (finger-has-key-place-p (inc column) (inc row))]
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
       (thumb-place 0 -1 web-post-tr)
       (thumb-place 0 -1 web-post-br)
       (thumb-place 0 -1 web-log-httr)
       (thumb-place 0 -1 web-log-htbr)
       (key-place 1 4 web-log-hbbl)
       (thumb-place 0 -1 web-log-htbr))
      (triangle-hulls
       (thumb-place 0 -1 web-post-tr)
       (thumb-place 0 -1 web-log-httr)
       (thumb-place 0 0 web-post-br)
       (thumb-place 0 0 web-log-htbr)
       (key-place 1 4 web-log-hbbl)
       (thumb-place 0 -1 web-log-httr))
      (triangle-hulls
       (key-place 1 4 web-log-hbbl)
       (thumb-place 0 0 web-log-htbr)
       (key-place 1 4 web-log-hbtl)
       (thumb-place 0 0 web-post-tr)
       (thumb-place 0 0 web-post-br))
      (triangle-hulls
       (key-place 1 4 web-log-hbbl)
       (key-place 1 4 web-post-bl)
       (key-place 1 4 web-log-hbtl)
       (key-place 1 4 web-post-tl)
       (key-place 0 3 web-post-br)
       (thumb-place 0 0 web-post-tr)
       (key-place 0 3 web-post-bl)
       (thumb-place 0 0 web-post-t)
       ;; (key-place 1 4 web-log-hbbl)
       ;; (thumb-place 0 0 web-post-br)
       ;; (key-place 1 4 web-post-tl)
       ;; (thumb-place 0 0 web-post-tr)
       ;; (key-place 1 3 web-post-bl)
       ;; (thumb-place 0 0 web-post-tr)
       ;; (key-place 0 3 web-post-br)
       ;; (thumb-place 0 0 web-post-tr)
       ;; (key-place 0 3 web-post-bl)
       ;; (thumb-place 0 0 web-post-tl)
       )
      #_(triangle-hulls (thumb-place 0 -1/2 thumb-br)
                      (key-place 1 4 web-post-bl)
                      (thumb-place 0 -1/2 thumb-tr)
                      (key-place 1 4 web-post-tl)
                      (key-place 1 3 web-post-bl)
                      (thumb-place 0 -1/2 thumb-tr)
                      (key-place 0 3 web-post-br)
                      (key-place 0 3 web-post-bl)
                      (thumb-place 0 -1/2 thumb-tr)
                      (thumb-place 0 -1/2 thumb-tl)
                      (key-place 0 3 web-post-bl)
                      (thumb-place 1 -1/2 thumb-tr)
                      (thumb-place 1 1 web-post-br)
                      (key-place 0 3 web-post-bl)
                      (key-place 0 3 web-post-tl)
                      (thumb-place 1 1 web-post-br)
                      (thumb-place 1 1 web-post-tr))
      ))))
