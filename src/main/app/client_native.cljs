(ns app.client-native
  "Entry point for native client."
  (:require
    [app.application :refer [SPA]]
    [com.fulcrologic.fulcro-native.expo-application-40 :as expo]
    [app.mobile-ui.root :as root]
    [app.mobile-ui.authentication :as auth]
    [app.mobile-ui.resources :as resources]
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.networking.http-remote :as net]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.ui-state-machines :as uism]
    [app.model.session :as session]))

;; See defines in shadow-cljs for dev mode
(goog-define SERVER_URL "http://production.server.com/api-native")

(defn ^:export start
  {:dev/after-load true}
  []
  (log/info "Re-mounting")
  (app/mount! @SPA root/Root :i-got-no-dom-node))

(defn init []
  (reset! SPA (expo/fulcro-app
                {:cached-images resources/images
                 :cached-fonts resources/fonts
                 :client-did-mount (fn [app]
                                     (uism/begin! app session/session-machine ::session/session
                                       {:actor/login-form      auth/Login
                                        :actor/current-session session/Session}))
                 :remotes          {:remote (net/fulcro-http-remote {:url        SERVER_URL
                                                                     :make-xhrio #(doto (net/make-xhrio)
                                                                                    (.setWithCredentials true))})}}))
  (start))
