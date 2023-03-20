(ns app.mobile-ui.root
  (:require
    [com.fulcrologic.fulcro-native.alpha.components :as c :refer [ui-text ui-button ui-view]]
    [com.fulcrologic.fulcro-native.events :refer [event-text]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
    [com.fulcrologic.fulcro.ui-state-machines :as uism]
    [app.mobile-ui.native-components-base :as b]
    [app.model.session :as session]))

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
    (b/ui-tab-view {:navigationState state
                    :renderScene     (b/ui-scene-map #js {:first  main-route
                                                          :second settings-route})
                    :key             "ui-tab-view-unique"
                    :renderTabBar    (fn [props]
                                       (b/ui-container {:mt   "10"
                                                        :maxW "100%"
                                                        :w    "100%"}
                                                       (b/ui-hstack {:justifyContent "space-between"
                                                                     :alignItems     "center"
                                                                     :w              "100%"}
                                                                    (b/ui-hstack {} "")
                                                                    (b/ui-hstack {} "")
                                                                    (b/ui-hstack {} (b/ui-button {:onPress     (fn []
                                                                                                                 (uism/trigger! this ::session/session :event/logout)
                                                                                                                 (dr/change-route this ["login"]))
                                                                                                  :hasText     true
                                                                                                  :transparent true} "Logout")))
                                                       (b/ui-box {:flexDirection "row"
                                                                  :w             "100%"}
                                                                 (doall
                                                                   (map-indexed
                                                                     (fn [idx route]
                                                                       (let [color (if (= @index idx) "#e5e5e5" "#1f2937")
                                                                             border-color (if (= @index idx) "cyan.500" "gray.400")]
                                                                         (b/ui-box {:borderBottomWidth "3"
                                                                                    :borderColor       border-color
                                                                                    :flex              "1"
                                                                                    :alignItems        "center"
                                                                                    :p                 "3"
                                                                                    :mt                "5"
                                                                                    :cursor            "pointer"
                                                                                    :key               (str "ui-box: fn route: " route ": idx" idx)}
                                                                                   (b/ui-pressable {:onPress (fn []
                                                                                                               (reset! index idx))}
                                                                                                   (b/ui-animated-text {:style color}
                                                                                                                       (get route "title"))))))
                                                                     (get-in (js->clj props) ["navigationState" "routes"]))))))
                    :onIndexChange   (fn [idx]
                                       (reset! index idx))})))

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
        loading? (= :state/checking-session current-state)]
    (let [{:keys [password]} (comp/get-state this)
          error? (boolean (seq error))
          login! (fn []
                   (uism/trigger! this ::session/session :event/login {:username email
                                                                       :password password}))]
      (b/ui-center {:flex "1" :px "3" :w "100%"}
                   (b/ui-container {:w "100%"}
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
                                                                                                                       (m/set-value! this :account/email (event-text evt)))}))

                                                                            (b/ui-stack {}
                                                                                        (b/ui-label {} "Password")
                                                                                        (b/ui-input {:secureTextEntry true
                                                                                                     :onSubmitEditing login!
                                                                                                     :variant         "underlined"
                                                                                                     :placeholder     "Password"
                                                                                                     :value           password
                                                                                                     :onChange        #(comp/set-state! this {:password (event-text %)})}))))
                                             (ui-bbutton {:label    (if loading? (b/ui-spinner {:size "sm"}) "Login")
                                                          :disabled loading?
                                                          :onPress  login!})
                                             (when error?
                                               (ui-error-card error))))))))

(defrouter RootRouter [_ _]
  {:router-targets [Login Main]})

(def ui-root-router (comp/factory RootRouter))

(defsc Root [this {:root/keys [router]}]
  {:query             [{:root/router (comp/get-query RootRouter)}]
   :componentDidMount (fn [this]
                        (dr/change-route this ["login"]))
   :initial-state     {:root/router {}}}
  (b/ui-base {:mt "50px"}
             (ui-root-router router)))
