(ns dick-tracy.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [com.rpl.specter :refer :all]
            [com.rpl.specter.macros :refer :all]))

(def WIDTH 1000)
(def HEIGHT 800)

(defn css-color [color] 
  (let [ colors (rest (clojure.string/split color #""))
        red (take 2 colors)
        green (take 2 (drop 2 colors))
        blue (take 2 (drop 4 colors))]
    (map #(-> (conj % "0x") (clojure.string/join) (read-string)) [red green blue])))

(def colors
  ;; last four are "accent" colors:
  (array-map
   :red ["#f44336" "#ffebee" "#ffcdd2" "#ef9a9a" "#e57373" "#ef5350" "#f44336" "#e53935" "#d32f2f" "#c62828" "#b71c1c" "#ff8a80" "#ff5252" "#ff1744" "#d50000"]
   :pink ["#e91e63" "#fce4ec" "#f8bbd0" "#f48fb1" "#f06292" "#ec407a" "#e91e63" "#d81b60" "#c2185b" "#ad1457" "#880e4f" "#ff80ab" "#ff4081" "#f50057" "#c51162"]
   :purple ["#9c27b0" "#f3e5f5" "#e1bee7" "#ce93d8" "#ba68c8" "#ab47bc" "#9c27b0" "#8e24aa" "#7b1fa2" "#6a1b9a" "#4a148c" "#ea80fc" "#e040fb" "#d500f9" "#aa00ff"]
   :deep-purple ["#673ab7" "#ede7f6" "#d1c4e9" "#b39ddb" "#9575cd" "#7e57c2" "#673ab7" "#5e35b1" "#512da8" "#4527a0" "#311b92" "#b388ff" "#7c4dff" "#651fff" "#6200ea"]
   :indigo ["#3f51b5" "#e8eaf6" "#c5cae9" "#9fa8da" "#7986cb" "#5c6bc0" "#3f51b5" "#3949ab" "#303f9f" "#283593" "#1a237e" "#8c9eff" "#536dfe" "#3d5afe" "#304ffe"]
   :blue ["#2196f3" "#e3f2fd" "#bbdefb" "#90caf9" "#64b5f6" "#42a5f5" "#2196f3" "#1e88e5" "#1976d2" "#1565c0" "#0d47a1" "#82b1ff" "#448aff" "#2979ff" "#2962ff"]
   :light-blue ["#03a9f4" "#e1f5fe" "#b3e5fc" "#81d4fa" "#4fc3f7" "#29b6f6" "#03a9f4" "#039be5" "#0288d1" "#0277bd" "#01579b" "#80d8ff" "#40c4ff" "#00b0ff" "#0091ea"]
   :cyan ["#00bcd4" "#e0f7fa" "#b2ebf2" "#80deea" "#4dd0e1" "#26c6da" "#00bcd4" "#00acc1" "#0097a7" "#00838f" "#006064" "#84ffff" "#18ffff" "#00e5ff" "#00b8d4"]
   :teal ["#009688" "#e0f2f1" "#b2dfdb" "#80cbc4" "#4db6ac" "#26a69a" "#009688" "#00897b" "#00796b" "#00695c" "#004d40" "#a7ffeb" "#64ffda" "#1de9b6" "#00bfa5"]
   :green ["#4caf50" "#e8f5e9" "#c8e6c9" "#a5d6a7" "#81c784" "#66bb6a" "#4caf50" "#43a047" "#388e3c" "#2e7d32" "#1b5e20" "#b9f6ca" "#69f0ae" "#00e676" "#00c853"]
   :light-green ["#8bc34a" "#f1f8e9" "#dcedc8" "#c5e1a5" "#aed581" "#9ccc65" "#8bc34a" "#7cb342" "#689f38" "#558b2f" "#33691e" "#ccff90" "#b2ff59" "#76ff03" "#64dd17"]
   :lime ["#cddc39" "#f9fbe7" "#f0f4c3" "#e6ee9c" "#dce775" "#d4e157" "#cddc39" "#c0ca33" "#afb42b" "#9e9d24" "#827717" "#f4ff81" "#eeff41" "#c6ff00" "#aeea00"]
   :yellow ["#ffeb3b" "#fffde7" "#fff9c4" "#fff59d" "#fff176" "#ffee58" "#ffeb3b" "#fdd835" "#fbc02d" "#f9a825" "#f57f17" "#ffff8d" "#ffff00" "#ffea00" "#ffd600"]
   :amber ["#ffc107" "#fff8e1" "#ffecb3" "#ffe082" "#ffd54f" "#ffca28" "#ffc107" "#ffb300" "#ffa000" "#ff8f00" "#ff6f00" "#ffe57f" "#ffd740" "#ffc400" "#ffab00"]
   :orange ["#ff9800" "#fff3e0" "#ffe0b2" "#ffcc80" "#ffb74d" "#ffa726" "#ff9800" "#fb8c00" "#f57c00" "#ef6c00" "#e65100" "#ffd180" "#ffab40" "#ff9100" "#ff6d00"]
   :deep-orange ["#ff5722" "#fbe9e7" "#ffccbc" "#ffab91" "#ff8a65" "#ff7043" "#ff5722" "#f4511e" "#e64a19" "#d84315" "#bf360c" "#ff9e80" "#ff6e40" "#ff3d00" "#dd2c00"]
   ;; no accent colors:
   :brown ["#795548" "#efebe9" "#d7ccc8" "#bcaaa4" "#a1887f" "#8d6e63" "#795548" "#6d4c41" "#5d4037" "#4e342e" "#3e2723"]
   :grey ["#9e9e9e" "#fafafa" "#f5f5f5" "#eeeeee" "#e0e0e0" "#bdbdbd" "#9e9e9e" "#757575" "#616161" "#424242" "#212121"]
   :blue-grey ["#607d8b" "#eceff1" "#cfd8dc" "#b0bec5" "#90a4ae" "#78909c" "#607d8b" "#546e7a" "#455a64" "#37474f" "#263238"]
   :black-white ["#000000" "#ffffff"]))


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
   :color-select? false
   :color (apply q/color (css-color (first (:blue colors))))})


(defn get-color [state point offset size]
  (let [cs (vals colors)
        x-ind (int (Math/floor (/ (- (:x point) (:x offset)) (:x size))))
        y-ind (int (Math/floor (/ (- (:y point) (:y offset)) (:y size))))
        col (try
              (nth (nth cs x-ind) y-ind)
              (catch java.lang.IndexOutOfBoundsException e nil))
        ]
    (if col
      ;; (apply q/color (css-color col))
      (apply q/color (css-color col))
      (:color state))))

;; (get-color {:color "blue"} {:x 100 :y 100} {:x 50 :y 100} {:x 45 :y 45})
;; (apply q/color '(233 30 99))



(defn draw-color-overlay [state]
  (doall (map-indexed (fn [row key] (doall (map-indexed #(do
                                                           (q/fill (apply q/color (css-color %2)))
                                                           (q/stroke 0 0 0)
                                                           (q/ellipse (+ 50 (* %1 30)) (+ 100 (* row 30)) 25 25))
                                                        (get colors key)))) (keys colors))))

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

(defn color-button [state loc size]
  (let [color (:color state)]
    (q/fill color)
    (q/stroke color)
    (q/ellipse (:x loc) (:y loc) (:x size) (:y size))))

(defn draw-pencil [state]
  (let [loc {:x 10 :y 7}
        size {:x 45 :y 45}
        in? (collision? state loc size)]
    (cond
      (and (not (q/mouse-pressed?)) in?) (do
                                           (q/cursor :hand)
                                           (q/shape (:hover (:pencil (:tools state))) (:x loc) (:y loc) (:x size) (:y size)))
      (and (q/mouse-pressed?) in?) (do
                                     (q/cursor :hand)
                                     (q/shape (:click (:pencil (:tools state))) (:x loc) (:y loc) (:x size) (:y size)))
      :else (do
              (if (not (:color-select? state)) (q/cursor :cross))
              (q/shape (:normal (:pencil (:tools state))) (:x loc) (:y loc) (:x size) (:y size))))))

(defn tool-tray [state]
  (q/fill 200 200 200)
  (q/stroke 255 0)
  (q/rect 5 5 (- WIDTH 10) 50)
  (draw-pencil state)
  (color-button state {:x 90 :y 30} {:x 45 :y 45}))

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

(defn draw-guide-line [state]
  (if (:drawing? state)
    (do
      (let [last-point (last (:points (last (:paths state))))]
        (q/stroke (:color state))
        (q/line (:x last-point) (:y last-point) (:x state) (:y state))))))

(defn mouse-moved [state event]
  (assoc state :x (:x event) :y (:y event)))

(defn update-state [state]
  (let [point {:x (:x state) :y (:y state)}
        is-drawing? (:drawing? state)
        toolbar? (collision? point {:x 5 :y 5} {:x (- WIDTH 10) :y 50})
        color? (collision? point {:x 90 :y 30} {:x 45 :y 45})
        color-overlay? (:color-select? state)]
    (do
      (if color-overlay? (q/cursor :hand))
      (cond
        (and (q/mouse-pressed?) color? (not color-overlay?)) (assoc state :color-select? true)
        (and (q/mouse-pressed?) (:color-select? state) color-overlay?) (assoc state :color-select? false :color (get-color state point {:x 50 :y 100} {:x 25 :y 25}))
        (and (q/mouse-pressed?) (not toolbar?) (not is-drawing?)) (assoc state :drawing? true
                                                                         :paths (conj (:paths state) {:stroke (:color state) :fill (:color state) :points [point]}))
        (and (q/mouse-pressed?) (not toolbar?) is-drawing?) (append-point state point)
        (q/key-pressed?) (assoc state :drawing? false :color-select? false)
        ;; (:drawing? state)
        :else state
        )))
  ;; state
  )


(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 255 255 255)
  (draw-paths state)
  (draw-guide-line state)
  (tool-tray state)
  (if (:color-select? state)
    (draw-color-overlay state))
  ;; (q/fill 0 0 255)
  ;; (q/ellipse (:x state) (:y state) 20 20)
  )

(q/defsketch dick-tracy
  :title "Dick Tracy"
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
