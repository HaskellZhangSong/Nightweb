(ns nightweb-desktop.window
  (:require [splendid.jfx :as jfx])
  (:import (javafx.scene.layout VBox Priority)
           (javafx.scene.control TabPane Tab)
           javafx.scene.web.WebView))

(defn create-tab
  "Creates a new tab."
  []
  (let [new-tab (Tab. "Browser")
        web-view (WebView.)
        web-engine (.getEngine web-view)
        history (.getHistory web-engine)]
    (VBox/setVgrow web-view Priority/ALWAYS)
    (.load web-engine "http://localhost:3000")
    (.setContent new-tab web-view)
    new-tab))

(defn add-tab
  "Creates a tab and adds it to the tab bar."
  [tab-bar]
  (let [index (- (.size (.getTabs tab-bar)) 1)
        new-tab (create-tab)]
    (.add (.getTabs tab-bar) index new-tab)
    (.select (.getSelectionModel tab-bar) index)))

(defn start-window
  "Launches a JavaFX window."
  []
  (jfx/jfx
    (let [window (VBox.)
          tab-bar (TabPane.)
          plus-tab (Tab. " + ")]
      (.setTabClosingPolicy
        tab-bar javafx.scene.control.TabPane$TabClosingPolicy/ALL_TABS)
      (.setClosable plus-tab false)
      (.add (.getTabs tab-bar) plus-tab)
      (jfx/defhandler :onSelectionChanged plus-tab (add-tab tab-bar))
      (.addListener (.getTabs tab-bar)
                    (reify javafx.collections.ListChangeListener
                      (onChanged [this change]
                        (let [tabs (butlast (.getTabs tab-bar))]
                          (doseq [tab tabs]
                            (.setClosable tab (> (count tabs) 1)))))))
      (add-tab tab-bar)
      (VBox/setVgrow tab-bar Priority/ALWAYS)
      (.setWidth jfx/primary-stage 1024)
      (.setHeight jfx/primary-stage 768)
      (jfx/add window [tab-bar])
      (jfx/show window)
      (.setOnCloseRequest jfx/primary-stage
                          (reify javafx.event.EventHandler
                            (handle [this event]
                              (java.lang.System/exit 0)))))))