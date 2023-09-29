(ns app.mobile-ui.root
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
    [com.fulcrologic.fulcro.ui-state-machines :as uism]
    [app.mobile-ui.native-components-base :as b]
    [app.mobile-ui.authentication :refer [Login]]
    [app.model.session :as session]))

(defn main-route []
  (b/ui-text {} "Main Stuff"))

(defn settings-route []
  (b/ui-text {} "Settings Stuff"))

(def index (atom 0))

(defsc Main [this {:keys [label] :as props}]
  {:query         [:label]
   :route-segment ["main"]
   :initial-state {:label "Main"}
   :ident         (fn [] [:component/id :main])}
  (let [state #js {:index  @index
                   :routes (array
                             #js {:key "first" :title "Main"}
                             #js {:key "second" :title "Settings"})}]
    (b/ui-box
      {:padding 20}
      (b/ui-button {:onPress     (fn []
                                   (uism/trigger! this ::session/session :event/logout)
                                   (dr/change-route this ["login"]))
                    :hasText     true
                    :transparent true} "Logout")
      (b/ui-text {} "Hello world"))))

(defrouter RootRouter [_ _]
  {:router-targets [Login Main]})

(def ui-root-router (comp/factory RootRouter))

(defsc Root [this {:root/keys [router]}]
  {:query             [{:root/router (comp/get-query RootRouter)}]
   :componentDidMount (fn [this]
                        (dr/change-route this ["login"]))
   :initial-state     {:root/router {}}}
  (when (seq router)
    (b/ui-base {:mt "50px"}
      (ui-root-router router))))
