;; Poe
;; Author: Christopher Olsen
;; Copyright 2013
;; License: GPLv3 (a copy should be included, if not visit 
;;          http://www.gnu.org/licenses/gpl.txt

(ns poe.treebuild
  (:gen-class)
  (:require [poe.dblayer :as dbl]))


;; make-tree-node and find-children mutually recurse to create the tree.
(declare find-children)
(defn make-tree-node [table name parent-name]
  ; this includes parent-name so that poems only need to have unique names 
  ; within a collection (when it's displayed it's queried by name + collection)
  {:table table :name name :children (find-children table name)
   :parent-name parent-name})

(defn get-node-name [node]
  (:name node))
(defn get-node-table [node]
  (:table node))
(defn get-node-children [node]
  (:children node))
(defn get-node-parent [node]
  ;; this is only the name of the parent node, not the node itself
  (:parent-name node))
                              
(defn find-children [table name]
  ;; gets a list of children names from dblayer.clj and calls make-tree-node
  ;; for each of them (with appropriate other values)
  (cond (= table  "root")
        (let [chldn-names (dbl/get-children table name)]
          ;(println "\n get root children \n")
          (map (fn [ch-name] (make-tree-node "author" (:name ch-name) name))
               chldn-names))
        (= table "author")
        (let [chldn-names (dbl/get-children table name)]
          ;(println "\n get author children \n")
          (map (fn [ch-name] (make-tree-node "collection" (:name ch-name) name))
               chldn-names))
        (= table "collection")
        (let [chldn-names (dbl/get-children table name)]
          ;(println "\n get collection children \n")
          (map (fn [ch-name] (make-tree-node "poem" (:name ch-name) name))
               chldn-names))
        :else
         ;(println "\n find-children returning nil \n" table name)
        nil))

;; this returns a tree of the form:
;; {:table: root :name all :parent-name whatever 
;;  :children {:table author :name Bill Nye....}}
;; at most 4 deep (root -> author -> collection -> poem)
;; this tree recursively includes all children, therefore it is the entire thing
(defn build-tree []
  (let [tree (make-tree-node "root" "all" "none")]
    ;(println "\n build-tree this is the tree: \n" tree) ;;for debugging
    tree))
