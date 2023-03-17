(ns app.client-native
  "Entry point for native client."
  (:require
    [app.application :refer [SPA]]
    [fulcro.fulcro-native-expo-substitute :as expo]
    [app.mobile-ui.root :as root]
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
                {:client-did-mount (fn [app]
                                     (uism/begin! app session/session-machine ::session/session
                                       {:actor/login-form      root/Login
                                        :actor/current-session session/Session}))
                 :remotes          {:remote (net/fulcro-http-remote {:url        SERVER_URL
                                                                     :make-xhrio #(doto (net/make-xhrio)
                                                                                    (.setWithCredentials true))})}}))
  (start))
