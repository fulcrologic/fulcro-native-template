(ns app.mobile-ui.native-base
  "Convenience wrappers for Native Base components"
  (:require
    [com.fulcrologic.fulcro-native.alpha.components :as c :refer [react-factory]]
    ["native-base" :refer [Container Header Item Input Icon Button Content
                           List ListItem CheckBox Left Body Right Label Card CardItem
                           Form Spinner Tab Tabs]]))

(def ui-body (react-factory Body))
(def ui-button (react-factory Button))
(def ui-card (react-factory Card))
(def ui-card-item (react-factory CardItem))
(def ui-checkbox (react-factory CheckBox))
(def ui-container (react-factory Container))
(def ui-content (react-factory Content))
(def ui-form (react-factory Form))
(def ui-header (react-factory Header))
(def ui-icon (react-factory Icon))
(def ui-input (c/wrap-text-input (react-factory Input)))
(def ui-item (react-factory Item))
(def ui-label (react-factory Label))
(def ui-left (react-factory Left))
(def ui-list (react-factory List))
(def ui-list-item (react-factory ListItem))
(def ui-right (react-factory Right))
(def ui-spinner (react-factory Spinner))
(def ui-tab (react-factory Tab))
(def ui-tabs (react-factory Tabs))

(defn ui-heading
  "A ui-header with the specified string as the single body element"
  [title]
  (ui-header {} (ui-body {} title)))

