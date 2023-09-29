(ns app.mobile-ui.root
  (:require
    ["react-native" :refer [Text View Button]]
    [com.fulcrologic.fulcro.algorithms.react-interop :refer [react-factory]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]))

(def ui-view (react-factory View))
(def ui-text (react-factory Text))
(def ui-button (react-factory Button))

(defsc Main [this {:ui/keys [n]}]
  {:query         [:ui/n]
   :route-segment ["main"]
   :initial-state {:ui/n 1}
   :ident         (fn [] [:component/id :main])}
  (ui-view {:padding 20}
    (ui-text {} "Hello World")
    (ui-button {:title   (str "N " n)
                :onPress (fn [] (m/set-integer!! this :ui/n :value (inc n)))})))

(defrouter RootRouter [_ _]
  {:router-targets [Main]})

(def ui-root-router (comp/factory RootRouter))

(defsc Root [this {:root/keys [router]}]
  {:query         [{:root/router (comp/get-query RootRouter)}]
   :initial-state {:root/router {}}}
  (when (seq router)
    (ui-root-router router)))
