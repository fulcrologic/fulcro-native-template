(ns app.mobile-ui.native-components-base
  "Convenience wrappers for Native Base components"
  (:require
    [com.fulcrologic.fulcro-native.alpha.components :as c :refer [react-factory]]
    ["react-native" :refer [Pressable Animated]]
    ["react-native-tab-view" :refer [TabView SceneMap]]
    ["native-base" :refer [NativeBaseProvider Container Text Center Heading Box Button Spinner FormControl Stack HStack Input]]))

(def ui-base (react-factory NativeBaseProvider))

(def ui-heading (react-factory Heading))

(def ui-box (react-factory Box))

(def ui-button (react-factory Button))

(def ui-center (react-factory Center))

(def ui-hstack (react-factory HStack))

(def ui-text (react-factory Text))

(def ui-container (react-factory Container))

(def ui-form-control (react-factory FormControl))

(def ui-label (react-factory FormControl.Label))

(def ui-stack (react-factory Stack))

(def ui-input (c/wrap-text-input (react-factory Input)))

(def ui-spinner (react-factory Spinner))

(def ui-tab-view (react-factory TabView))

(def ui-animated-text (react-factory Animated.Text))

(def ui-pressable (react-factory Pressable))

(def ui-scene-map (partial SceneMap))
