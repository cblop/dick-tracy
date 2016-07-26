(ns dick-tracy.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

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
   :paths []
   :x 0
   :y 0
   :drawing? false
   :color 0
   :angle 0})

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
      (and (not (q/mouse-pressed?)) in?)  (q/shape (:hover (:pencil (:tools state))) (:x loc) (:y loc) (:x size) (:y size))
      (and (q/mouse-pressed?) in?) (q/shape (:click (:pencil (:tools state))) (:x loc) (:y loc) (:x size) (:y size))
      :else (q/shape (:normal (:pencil (:tools state))) (:x loc) (:y loc) (:x size) (:y size)))))

(defn tool-tray [state]
  (q/fill 200 200 200)
  (q/stroke 255 0)
  (q/rect 5 5 (- WIDTH 10) 50)
  (draw-pencil state))

(defn mouse-moved [state event]
  (assoc state :x (:x event) :y (:y event)))

(defn update-state [state]
  ; Update sketch state by changing circle color and position.
  (cond
    q/mouse-pressed? (assoc state :drawing (not (:drawing? state)))
    ;; (:drawing? state)
    )
  )

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 255 255 255)
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
