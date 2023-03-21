(ns app.mobile-ui.mobile-components
  (:require
    [com.fulcrologic.fulcro-native.alpha.components :refer [ui-text ui-button ui-view ui-image]]
    [app.mobile-ui.native-components-base :as b]))

(defn ui-error-card
  "Renders a Native Base card with the given message in it."
  [message]
  (b/ui-box {}
            (ui-text {:style {:color "red"}} message)))

(defn ui-bbutton [{:keys [key label onPress]}]
  (b/ui-button {:block   true
                :key     key
                :onPress (fn [evt] (when onPress (onPress evt)))
                :style   {:margin 10}} label))
