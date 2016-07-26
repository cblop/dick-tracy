(ns dick-tracy.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def WIDTH 800)
(def HEIGHT 600)

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  ;; (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:tools {:pencil (q/load-shape "resources/pencil.svg")}
   :color 0
   :angle 0})

(defn draw-tool [state]
  (q/fill 255 0 0)
  (q/ellipse 35 30 50 50)
  (q/shape (:pencil (:tools state)) 20 -49 50 50))

(defn tool-tray [state]
  (q/fill 200 200 200)
  (q/stroke 255 0)
  (q/rect 5 5 (- WIDTH 10) 50)
  (draw-tool state))

(defn update-state [state]
  ; Update sketch state by changing circle color and position.
  {:tools (:tools state)})

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 255 255 255)
  (tool-tray state))

(q/defsketch dick-tracy
  :title "You spin my circle right round"
  :size [500 500]
  ;; :settings #(q/smooth 4)
  ; setup function called only once, during sketch initialization.
  :setup setup
  :renderer :p2d
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
