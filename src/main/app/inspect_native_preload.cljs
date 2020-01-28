(ns app.inspect-native-preload
  (:require
    [com.fulcrologic.fulcro.inspect.inspect-client :as inspect]
    [com.fulcrologic.fulcro.inspect.inspect-ws :as inspect-ws]
    [taoensso.timbre :as log]))

(when-not @inspect/started?*
  (log/info "Installing Fulcro 3.x Inspect over Websockets targeting port " inspect-ws/SERVER_PORT)
  (reset! inspect/started?* true)
  ;; Workaround for https://github.com/ptaoussanis/sente/issues/361
  (inspect-ws/start-ws-messaging! {:channel-type :ajax}))