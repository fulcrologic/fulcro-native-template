(ns app.client
  "Entry point for web client"
  (:require
    [app.application :refer [SPA]]
    [app.ui.root :as root]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.networking.http-remote :as net]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [taoensso.timbre :as log]))

(def secured-request-middleware
  (->
    (net/wrap-csrf-token (or (.-fulcro_network_csrf_token js/window) "TOKEN-NOT-IN-HTML!"))
    (net/wrap-fulcro-request)))

(defn ^:export refresh []
  (log/info "Hot code Remount")
  (app/mount! @SPA root/Root "app"))

(defn ^:export init []
  (log/info "Application starting.")
  (reset! SPA (app/fulcro-app
                {:remotes {:remote (net/fulcro-http-remote
                                     {:url                "/api"
                                      :request-middleware secured-request-middleware})}}))
  (app/set-root! @SPA root/Root {:initialize-state? true})
  (dr/initialize! @SPA)
  (dr/change-route @SPA ["main"])
  (app/mount! @SPA root/Root "app" {:initialize-state? false}))

