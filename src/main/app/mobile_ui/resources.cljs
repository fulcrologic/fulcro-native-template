(ns app.mobile-ui.resources)

(defonce profile-blank (js/require "../assets/images/profile.png"))

(defonce images [profile-blank])

(defonce fonts [["Roboto_bold" (js/require "../assets/fonts/Roboto-Bold.ttf")]
                ["Roboto_medium" (js/require "../assets/fonts/Roboto-Medium.ttf")]])