(ns app.client
  "Entry point for web client"
  (:require
    [app.application :refer [SPA]]
    [com.fulcrologic.fulcro.application :as app]
    [app.ui.root :as root]
    [com.fulcrologic.fulcro.networking.http-remote :as net]
    [com.fulcrologic.fulcro.ui-state-machines :as uism]
    [com.fulcrologic.fulcro-css.css-injection :as cssi]
    [app.model.session :as session]
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]))

(defn ^:export refresh []
  (log/info "Hot code Remount")
  (cssi/upsert-css "componentcss" {:component root/Root})
  (app/mount! @SPA root/Root "app"))

(defn ^:export init []
  (log/info "Application starting.")
  (reset! SPA (app/fulcro-app
                {:remotes {:remote (net/fulcro-http-remote {:url "/api"})}}))
  (cssi/upsert-css "componentcss" {:component root/Root})
  (app/set-root! @SPA root/Root {:initialize-state? true})
  (dr/initialize! @SPA)
  (log/info "Starting session machine.")
  (uism/begin! @SPA session/session-machine ::session/session
    {:actor/login-form      root/Login
     :actor/current-session session/Session})
  (dr/change-route @SPA ["main"])
  (app/mount! @SPA root/Root "app" {:initialize-state? false}))

