(ns app.server-components.middleware
  (:require
    [app.server-components.config :refer [config]]
    [app.server-components.pathom :refer [parser]]
    [mount.core :refer [defstate]]
    [com.fulcrologic.fulcro.server.api-middleware :refer [handle-api-request
                                                          wrap-transit-params
                                                          wrap-transit-response]]
    [reitit.ring :as ring]
    [ring.middleware.defaults :refer [wrap-defaults]]
    [ring.middleware.gzip :refer [wrap-gzip]]
    [ring.middleware.session.memory :as mem]
    [ring.util.response :refer [response file-response resource-response]]
    [ring.util.response :as resp]
    [hiccup.page :refer [html5]]
    [taoensso.timbre :as log]))

(def ^:private not-found-handler
  (fn [req]
    {:status  404
     :headers {"Content-Type" "text/plain"}
     :body    "NOPE"}))

(defn api-handler [request]
  (handle-api-request
    (:transit-params request)
    (fn [tx] (parser {:ring/request request} tx))))

;; ================================================================================
;; Dynamically generated HTML. We do this so we can safely embed the CSRF token
;; in a js var for use by the client.
;; ================================================================================
(defn index [{:keys [anti-forgery-token]}]
  (log/debug "Serving index.html")
  (html5
    [:html {:lang "en"}
     [:head {:lang "en"}
      [:title "Application"]
      [:meta {:charset "utf-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"}]
      [:link {:href "https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.4.1/semantic.min.css"
              :rel  "stylesheet"}]
      [:link {:rel "shortcut icon" :href "data:image/x-icon;," :type "image/x-icon"}]
      [:script (str "var fulcro_network_csrf_token = '" anti-forgery-token "';")]]
     [:body
      [:div#app]
      [:script {:src "js/main/main.js"}]]]))

(defstate middleware
  :start
  (let [session-store          (mem/memory-store)
        defaults-config        (-> (:ring.middleware/defaults-config config)
                                 ;; If you want to set something like session store, you'd do it against
                                 ;; the defaults-config here (which comes from an EDN file, so it can't have
                                 ;; code initialized).
                                 (assoc-in [:session :store] session-store))
        defaults-config-native (-> defaults-config
                                 ;; CSRF makes sense only in browser, so we disable it for native
                                 ;; and change cookie name to prevent CSRF against this endpoint
                                 (assoc-in [:security :anti-forgery] false)
                                 (update-in [:session :cookie-name] str "-native"))]
    (ring/ring-handler
      (ring/router
        [["/api"
          {:middleware [#(wrap-defaults % defaults-config)
                        wrap-transit-response
                        wrap-transit-params]
           :post       {:summary "Fulcro API"
                        :handler api-handler}}]
         ["/api-native"
          {:middleware [#(wrap-defaults % defaults-config-native)
                        wrap-transit-response
                        wrap-transit-params]
           :post       {:summary "Fulcro API for Native"
                        :handler api-handler}}]])
      (-> (fn [req]
            (-> (resp/response (index req))
              (resp/content-type "text/html")))
        (wrap-defaults defaults-config))
      {:middleware [wrap-gzip]})))
