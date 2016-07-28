(ns dick-tracy.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [com.rpl.specter :refer :all]
            [com.rpl.specter.macros :refer :all]))

(def WIDTH 800)
(def HEIGHT 600)

(defn setup []
  ; Set frame rate to 30 frames per second.
  ;; (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  ;; (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  (q/cursor :cross)
  ; circle color and position.
  {:tools {:pencil {:normal (q/load-shape "resources/pencil.svg")
                    :hover (q/load-shape "resources/pencil-hover.svg")
                    :click (q/load-shape "resources/pencil-click.svg")}}
   :paths [
           {:stroke (q/color 255 0 0)
            :fill (q/color 255 255 255)
            :points [{:x 0 :y 0}
                     {:x 100 :y 100}
                     {:x 250 :y 600}]}
           {:stroke (q/color 0 255 0)
            :fill (q/color 0 255 255)
            :points [{:x 800 :y 0}
                     {:x 700 :y 100}
                     {:x 600 :y 200}]}
           {:stroke (q/color 0 0 255)
            :fill (q/color 0 255 0)
            :points [{:x 500 :y 1000}
                     {:x 300 :y 900}
                     {:x 650 :y 200}]}
           ]
   :x 0
   :y 0
   :drawing? false
   :color 0
   :angle 0})

(defn append-point [state point]
  (transform [:paths LAST :points] #(conj % point) state))

(defn add-point-to-path [point path]
  )

(defn update-paths [paths index path])

(defn collision? [loc anchor size]
  (and
   (> (:x loc) (:x anchor))
   (> (:y loc) (:y anchor))
   (< (:x loc) (+ (:x anchor) (:x size)))
   (< (:y loc) (+ (:y anchor) (:y size)))))

(defn draw-pencil [state]
  (let [loc {:x 10 :y 7}
        size {:x 45 :y 45}
        in? (collision? state loc size)]
    (cond
      (and (not (q/mouse-pressed?)) in?) (q/shape (:hover (:pencil (:tools state))) (:x loc) (:y loc) (:x size) (:y size))
      (and (q/mouse-pressed?) in?) (q/shape (:click (:pencil (:tools state))) (:x loc) (:y loc) (:x size) (:y size))
      :else (q/shape (:normal (:pencil (:tools state))) (:x loc) (:y loc) (:x size) (:y size)))))

(defn tool-tray [state]
  (q/fill 200 200 200)
  (q/stroke 255 0)
  (q/rect 5 5 (- WIDTH 10) 50)
  (draw-pencil state))

(defn draw-path [{:keys [stroke fill points] :as path}]
  (q/fill fill)
  (q/stroke stroke)
  (q/begin-shape)
  (doseq [p points]
    (q/vertex (:x p) (:y p)))
  (q/end-shape :close)
  )

(defn draw-paths [state]
  (let [paths (:paths state)]
    (doseq [p paths] (draw-path p))
    ))

(defn mouse-moved [state event]
  (assoc state :x (:x event) :y (:y event)))

(defn update-state [state]
  (let [point {:x (:x state) :y (:y state)}
        is-drawing? (:drawing? state)
        toolbar? (collision? point {:x 5 :y 5} {:x (- WIDTH 10) :y 50})]
    (cond
      (and (q/mouse-pressed?) (not toolbar?) (not is-drawing?)) (assoc state :drawing? true
                                                      :paths (conj (:paths state) {:stroke (q/color 0 0 0) :fill (q/color 50 50 50) :points [point]}))
      (and (q/mouse-pressed?) (not toolbar?) is-drawing?) (append-point state point)
      (q/key-pressed?) (assoc state :drawing? false)
      ;; (:drawing? state)
      :else state
      ))
  ;; state
  )


(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 255 255 255)
  (draw-paths state)
  (tool-tray state)
  ;; (q/fill 0 0 255)
  ;; (q/ellipse (:x state) (:y state) 20 20)
  )

(q/defsketch dick-tracy
  :title "You spin my circle right round"
  :size [WIDTH HEIGHT]
  ;; :settings #(q/smooth 4)
  ; setup function called only once, during sketch initialization.
  :setup setup
  ;; :renderer :p3d
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  :mouse-moved mouse-moved
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
