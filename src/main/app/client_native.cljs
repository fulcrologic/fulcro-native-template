(ns app.client-native
  "Entry point for native client.

   Here are the ideas:

   1. I read the expo startup code. All it does for native mobile apps is call AppRegistry.registerComponent. So, I just
      do that manually in this ns.
   2. I used react hooks for a wrapper around what Fulcro wants to render (the arg to render-root). I added local state there so I could
      make a function to force a re-render of the new UI with the current state
  "
  (:require
    ["react-native" :as native :refer [Text Button View AppRegistry Platform]]
    [app.application :refer [SPA]]
    [app.mobile-ui.root :as root]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.networking.http-remote :as net]
    [com.fulcrologic.fulcro.react.hooks :as hooks]
    [com.fulcrologic.fulcro.rendering.keyframe-render :as kr]
    [taoensso.timbre :as log]))

;; Tracking for what Fulcro wants to render. We don't want to close over a constant value because then hot code
;; reloads would not see updates.
(defonce root-ref (volatile! nil))
;; In order for Fulcro to remount things, we have to return the root component from render
(defonce root-component-ref (volatile! nil))

;; We need a way to externally see the react set state hook function, so we can force the wrapper to re-render at will
(defonce force-render-atom (volatile! identity))
(defn force-render!
  "Update the component local state of the root wrapper to force a root-level re-render."
  []
  (comp/enable-forced-refresh! 1000)
  (@force-render-atom))

(defn render-root
  "A function that can be used as the main Fulcro render for an expo app. Registers the app on the first render. Any
   subsequence calls just update a global atom that is tracking the element Fulcro is trying to render, which is used
   in the closure of the internal generated wrapper. This is so hot code reload refreshes the UI without reloading the app."
  [root]
  (try
    (let [first-call? (nil? @root-ref)]
      (vreset! root-ref root)

      (if first-call?
        (let [RootWrapper (fn [this]
                            (let [body @root-ref
                                  [n render!] (hooks/use-state 0)]
                              (vreset! force-render-atom (fn [] (render! (inc n))))
                              (vreset! root-component-ref this)
                              (try
                                (if (fn? body)
                                  (body)
                                  body)
                                (catch :default e
                                  (log/error e "Render failed")))))]
          (native/AppRegistry.registerComponent "mobile" (fn [] RootWrapper)))
        (do
          (@force-render-atom)
          @root-component-ref)))
    (catch :default e
      (log/error e "Unable to mount/refresh"))))

;; See defines in shadow-cljs for dev mode
(goog-define SERVER_URL "http://production.server.com/api-native")

;; Modify the Fulcro app to use our React-native rendering wrapper
(defonce APP
  (app/fulcro-app
    {:render-root!      render-root
     :hydrate-root!     (fn [& _])
     :optimized-render! kr/render!
     :remotes           {:remote (net/fulcro-http-remote {:url        SERVER_URL
                                                          :make-xhrio #(doto (net/make-xhrio)
                                                                         (.setWithCredentials true))})}}))

(defn start []
  (reset! SPA APP)
  (app/mount! APP root/Root :ignored-in-native))

(defn ^:export init [] (start))
