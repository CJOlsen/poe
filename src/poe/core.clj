;; Poe
;; Author: Christopher Olsen
;; Copyright 2013
;; License: GPLv3 (a copy should be included, if not visit 
;;          http://www.gnu.org/licenses/gpl.txt

(ns poe.core
  (:gen-class)
  (:use [seesaw core tree font mig])
  (:require [poe.dblayer :as dbl]
            [poe.treebuild :as tbld]))


;;
;; tree section
;;

;; see treebuild.clj (tbld) and tree example at:
;; https://github.com/daveray/seesaw/blob/develop/test/seesaw/test/examples/explorer.clj
;;   - unfortunately the tree doesn't support updates at this time and
;;     it will take some work to make a new version that will

(defn is-branch? [node]
  ;; (partial = ...) avoids a type coercion error, some checks membership
  (not (nil? (some (partial = (:table node))
                   ["root" "author" "collection"]))))

(def tree-model
  ; this translates the custom tree into a "simple-tree-model" for display
  ; (simple-tree-model branch? children root)  <-- built-in, see docs
  (simple-tree-model is-branch? 
                     (fn [node] (tbld/get-node-children node))
                     (tbld/build-tree)))

(defn render-node
  ; called by tree in nav-window, tree names/icons can be customized here
  [renderer {:keys [value]}]
  (config! renderer :text (:name value)))

;;
;; layout section
;;

(def nav-window
  (tree :id :tree :model tree-model :renderer render-node))

(def disp-window
  (scrollable(text :text ""
                   :multi-line? true
                   :id :the-text)))

(def remove-selected-button
  (button :text "Remove Selected"))

(def new-collection-button
  (button :text "New Collection"))

(def new-author-button
  (button :text "New Author"))

(def import-button
  (button :text "Import Poem"))

(def new-button
  (button :text "New/Clear"))

(def save-button
  (button :text "Save"))

(def nav-buttons
  (vertical-panel :items [remove-selected-button new-collection-button
                          new-author-button import-button]))
(def disp-buttons
  (horizontal-panel :items [new-button save-button]))

(def nav-split 
  (top-bottom-split (scrollable nav-window)
                    nav-buttons
                    :divider-location 3/4))
(def disp-split 
  (top-bottom-split disp-window
                    disp-buttons
                    :divider-location 99/100))
(def main-split
  (left-right-split nav-split
                    disp-split
                    :divider-location 1/3))

(def main-panel
  (border-panel
   :north (label :id :location :text "new" :halign :right)
   :center main-split
   :south (label :id :status :text "good day for poetry...")
   :vgap 5 :hgap 5 :border 5))

;;
;; logic section
;;

(defn display-poem [poem-name collection-name]
  (let [text (dbl/get-poem poem-name collection-name)]
    (config! (select disp-window [:#the-text]) :text text) ;;magic
    (scroll! disp-window :to :top) ;; how to scroll left?
    (config! (select main-panel [:#location])
             :text (str "Author: " (dbl/get-collection-author collection-name)
                        "   Collection: " collection-name
                        "   Poem: " poem-name)))) 

(defn save-current-poem! [author collection name]
  (dbl/save-poem! name
                  (text (select disp-window [:#the-text]))
                  collection
                  author))
  
(defn display-save-dlg []
  ;; triggered by the save button (note: this  was a mess 80 char wide)
  ;; this 'let' allows the dropdowns to be accessed in the save :handler 
  ;; function, which is where the business of calling out to the database layer 
  ;; takes place (via save-current-poem).  there may be a better solution 
  ;; involving the 'bind' capabilities of core.bind, but it it's hard to say one 
  ;; way or another and this a) works and b) isn't obviously less clear than the 
  ;; alternatives
  (let [author-dropdown (combobox :id :author-box :model (dbl/get-authors))
        collection-dropdown (combobox :id :collection-box :model (dbl/get-collections))
        poem-name (text :id :poem-name :text "untitled" :columns 15)]
    (-> (dialog
         :options [(action :name "Save"
                           ;; saves the data before returning
                           :handler (fn [e] (do (save-current-poem! (text author-dropdown)
                                                                    (text collection-dropdown)
                                                                    (text poem-name))
                                                (return-from-dialog e :save))))
                   (action :name "Cancel"
                           :handler (fn [e] (return-from-dialog e :cancel)))]
         :content (mig-panel :items [[(label :font (font :from (default-font "Label.font")
                                                         :style :bold)
                                             :text "Save poem?") "gaptop 10, wrap"]
                                     [:separator "growx, wrap, gaptop 10, spanx 2"]
                                     ["Author:"]
                                     [author-dropdown "wrap"]
                                     ["Collection:"]
                                     [collection-dropdown "wrap"]
                                     ["Poem name:"]
                                     [poem-name]])
         :success-fn (fn [pane] ())) pack! show!))) 

;;
;; button logic
;;

(listen remove-selected-button :action
        ;; remove selected poem(s), author(s) or collections(s) from database
        (fn [e] (println "remove selected button pressed")))

(listen new-collection-button :action
        ;; make new collection
        ;; these could be wrapped up into one dialog box 
        (fn [e] (if-let [collection (input "Enter new collection name")]
                  (if-let [author (input (str "Choose author for collection:\n"
                                              collection
                                              "\n(will show up on restart)")
                                         :choices (dbl/get-authors))]
                    (dbl/save-collection! collection author)))))

(listen new-author-button :action
        ;; make new author
        (fn [e] (if-let [author (input "Enter new author name")]
                  (dbl/save-author! author))))

(listen import-button :action
        ;; pop-up import window
        (fn [e] (input "import isn't implemented yet\n"
                       "copy/paste into main window to add new poem")))

(listen new-button :action
        ;; clear window
        (fn [e]
          (do (text! (select disp-window [:#the-text]) " ")
              (config! (select main-panel [:#location]) :text "new"))))

(listen save-button :action
        ;; save current displayed poem
        ;; should display info in header if save sucessful
        (fn [e] (display-save-dlg)))

(defn get-path [node]
  ;; display value for the footer
  ;; this should really be embedded in the tree/node structure
  (cond (= "poem" (:table node))
        (str "/" (dbl/get-collection-author (:parent-name node))
             "/" (:parent-name node) "/")
        (= "author" (:table node))
        (str "/" (:name node) "/")
        (= "collection" (:table node))
        (str "/" (:parent-name node) "/" (:name node))))

;; listen for tree selection operations
(listen (select nav-window [:#tree]) :selection
        (fn [e]
          (let [current (last (selection e))]
            (config! (select main-panel [:#status]) :text (get-path current))
            (if (= "poem" (:table current))
              (display-poem (:name current) (:parent-name current))))))


;;
;; main
;;

(defn -main [& args]
  (native!)
  (-> (frame :title "poe: your poetry database"
             :content main-panel
             :width 850
             :height 500
             :on-close :exit)
      show!))
