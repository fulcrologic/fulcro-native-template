(ns app.ui.root
  (:require
    [com.fulcrologic.fulcro.components  :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))

(defsc Root [this {:ui/keys [n]}]
  {:query         [:ui/n]
   :initial-state {:ui/n 42}}
  (dom/h3 {}
    (str "Hello world. " n)))
