(ns app.mobile-ui.root
  (:require
    [com.fulcrologic.fulcro-native.alpha.components :refer [react-factory ui-text ui-view]]
    [com.fulcrologic.fulcro-native.events :refer [event-text]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
    [com.fulcrologic.fulcro.ui-state-machines :as uism]
    [app.mobile-ui.native-base :as b]
    [app.model.session :as session]))

(defn ui-error-card
  "Renders a Native Base card with the given message in it."
  [message]
  (b/ui-card {}
    (b/ui-card-item {}
      (b/ui-body {}
        (ui-text {:style {:color "red"}} message)))))

(defn ui-bbutton [{:keys [key label onPress]}]
  (b/ui-button {:block   true
                :key     key
                :onPress (fn [evt] (when onPress (onPress evt)))
                :style   {:margin 10}} label))

(defsc Main [this {:keys [label] :as props}]
  {:query         [:label]
   :initial-state {:label "Main"}
   :route-segment ["main"]
   :ident         (fn [] [:component/id :main])}
  (b/ui-container {}
    (b/ui-header {}
      (b/ui-left {} "")
      (b/ui-body {} label)
      (b/ui-right {} (b/ui-button {:onPress     (fn []
                                                  (uism/trigger! this ::session/session :event/logout)
                                                  (dr/change-route this ["login"]))
                                   :hasText     true
                                   :transparent true} "Logout")))
    (b/ui-tabs {}
      (b/ui-tab {:heading "Main"}
        (ui-view {} "Main Stuff"))
      (b/ui-tab {:heading "Settings"}
        (ui-view {} "Settings Stuff")))))

(defsc Login [this {:account/keys [email]
                    :ui/keys      [error] :as props}]
  {:query          [:ui/error :account/email
                    {[:component/id :session] (comp/get-query session/Session)}
                    [::uism/asm-id ::session/session]]
   :route-segment  ["login"]
   :initial-state  {:account/email "" :ui/error ""}
   :initLocalState (fn [this] {:password ""})
   :ident          (fn [] [:component/id :login])}
  (let [current-state (uism/get-active-state this ::session/session)
        loading?      (= :state/checking-session current-state)]
    (let [{:keys [password]} (comp/get-state this)
          error? (boolean (seq error))
          login! (fn []
                   (uism/trigger! this ::session/session :event/login {:username email
                                                                       :password password}))]
      (b/ui-container {}
        (b/ui-heading "Please Log In")
        (b/ui-content {}
          (b/ui-form {}
            (b/ui-item {:stackedLabel true}
              (b/ui-label {} "Username")
              (b/ui-input {:value          email
                           :autoCapitalize "none"
                           :autoCorrect    false
                           :onChange       (fn [evt]
                                             (m/set-value! this :account/email (event-text evt)))}))
            (b/ui-item {:stackedLabel true}
              (b/ui-label {} "Password")
              (b/ui-input {:secureTextEntry true
                           :onSubmitEditing login!
                           :value           password
                           :onChange        #(comp/set-state! this {:password (event-text %)})})))
          (ui-bbutton {:label    (if loading? (b/ui-spinner {}) "Login")
                       :disabled loading?
                       :onPress  login!})
          (when error?
            (ui-error-card error)))))))

(defrouter RootRouter [_ _]
  {:router-targets [Login Main]})

(def ui-root-router (comp/factory RootRouter))

(defsc Root [this {:root/keys [router]}]
  {:query             [{:root/router (comp/get-query RootRouter)}]
   :componentDidMount (fn [this]
                        (dr/change-route this ["login"]))
   :initial-state     {:root/router {}}}
  (ui-root-router router))
