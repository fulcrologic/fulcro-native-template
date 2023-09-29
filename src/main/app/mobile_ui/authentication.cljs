(ns app.mobile-ui.authentication
  (:require
    [com.fulcrologic.fulcro-native.alpha.components :refer [ui-text ui-button ui-view ui-image]]
    [app.mobile-ui.mobile-components :refer [ui-bbutton ui-error-card]]
    [app.mobile-ui.resources :refer [profile-blank]]
    [com.fulcrologic.fulcro-native.events :refer [event-text]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.ui-state-machines :as uism]
    [app.mobile-ui.native-components-base :as b]
    [app.model.session :as session]
    [taoensso.timbre :as log]))

(defsc Login [this {:account/keys [email]
                    :ui/keys      [error] :as props}]
  {:query          [:ui/error :account/email
                    {[:component/id :session] (comp/get-query session/Session)}
                    [::uism/asm-id ::session/session]]
   :route-segment  ["login"]
   :initial-state  {:account/email "" :ui/error ""}
   :initLocalState (fn [this] {:password ""})
   :ident          (fn [] [:component/id :login])}
  (log/info email)
  (let [current-state (uism/get-active-state this ::session/session)
        loading?      (= :state/checking-session current-state)
        {:keys [password]} (comp/get-state this)
        error?        (boolean (seq error))
        login!        (fn []
                        (uism/trigger! this ::session/session :event/login {:username email
                                                                            :password password}))]
    (b/ui-center {:flex "1" :px "3" :w "100%"}
      (b/ui-container {:w "100%"}
        (b/ui-center {:maxW "50px"
                      :h    "auto"}
          (ui-image {:style
                     {:width  50
                      :height 50}
                     :source profile-blank}))
        (b/ui-heading {:py "5"} "Please Log In")
        (b/ui-box {:w "50%"}
          (b/ui-form-control {}
            (b/ui-stack {:space 5}
              (b/ui-stack {}
                (b/ui-label {} "Username")
                (b/ui-input {:value          email
                             :autoCapitalize "none"
                             :variant        "underlined"
                             :placeholder    "Username"
                             :autoCorrect    false
                             :onChange       (fn [evt]
                                               (m/set-value! this :account/email
                                                 (event-text evt)))}))

              (b/ui-stack {}
                (b/ui-label {} "Password")
                (b/ui-input
                  {:secureTextEntry true
                   :onSubmitEditing login!
                   :variant         "underlined"
                   :placeholder     "Password"
                   :value           password
                   :onChange        #(comp/set-state! this {:password (event-text %)})}))))
          (ui-bbutton {:label    (if loading? (b/ui-spinner {:size "sm"}) "Login")
                       :disabled loading?
                       :onPress  login!})
          (when error?
            (ui-error-card error)))))))
