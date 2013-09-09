;; Poe
;; Author: Christopher Olsen
;; Copyright 2013
;; License: GPLv3 (a copy should be included, if not visit 
;;          http://www.gnu.org/licenses/gpl.txt

(ns poe.dblayer
  (:gen-class)
  (:require [clojure.java.jdbc :as j]
            [clojure.java.jdbc.sql :as s]))


;;
;; db administration
;;

(def db
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname "db/database.db"})

(defn no-table? [table-name]
  ;; pull all the table names, check membership with 'some'
    (let [tables (j/query 
                  db
                  ["SELECT name FROM sqlite_master WHERE type = \"table\""])]
      (nil? (some #(= table-name %)
                  (map :name tables)))))

(defn create-db []
  (j/with-connection db
    (do
      (if (no-table? "author")
        (j/create-table "author"
                        [:name :text "PRIMARY KEY"]))
      (if (no-table? "collection")
        (j/create-table "collection"
                        [:name :text]
                        [:year "INTEGER"]
                        [:type :text]
                        [:author :text "references author (name)"]
                        ["PRIMARY KEY" "(name, author)"]))
      (if (no-table? "poem")
        (j/create-table "poem"
                        [:name :text]
                        [:body :text]
                        [:collection :text "references collection (name)"]
                        [:author :text "references author (name)"]
                        ["PRIMARY KEY" "(name, collection)"]))
      nil)))

;;
;; interface methods (for core.clj)
;;

(defn get-children [table name]
  ;; get node children for the navigation tree **BROKEN**
  ;; **** doesn't take into account that two authors can have collections with
  ;; **** the same names.
  (cond (= table "root")
        (j/query db (s/select :name :author))
        (= table "author")
        (j/query db (s/select :name :collection (s/where {:author name})))
        (= table "collection")
        (j/query db (s/select :name :poem (s/where {:collection name})))))

(defn get-poem [poem-name collection-name]
  (:body (first
          (j/query db (s/select :body :poem
                                (s/where {:name poem-name
                                          :collection collection-name}))))))

(defn get-collection-author [collection-name]
  (:author (first (j/query db (s/select :author :collection
                                        (s/where {:name collection-name}))))))

(defn get-authors []
  (map :name (j/query db (s/select :name :author))))

(defn get-collections []
  (map :name (j/query db (s/select :name :collection))))

(defn save-poem! [name body collection author]
  ;; update-or-insert-values depends on the deprecated with-connection macro...
  (try
    (j/insert! db :poem {:name name :body body :collection collection
                         :author author})
    (catch Exception e
      ;; if it was a primary key error update! should work
      (j/update! db :poem {:name name :body body :collection collection
                           :author author} 
                 (s/where {:name name :collection collection})))))
                 
(defn save-collection! [name author]
  (j/insert! db :collection {:name name :author author}))

(defn save-author! [name]
  (j/insert! db :author {:name name}))

(defn delete-poem! [name collection]
  (j/delete! db :poem (s/where {:name name :collection collection})))

(defn delete-collection! [name author]
  (j/delete! db :collection (s/where {:name name :author author})))

(defn delete-author! [name]
  (j/delete! db :author (s/where {:name name})))




;; (defn initialize-db []
;;   (create-db)
;;   (add-default-data))
;; (initialize-db)