(ns git
  (:require 
   [babashka.tasks :refer [shell]]
   [babashka.fs :as fs]))

(def base "/home/florian/repo/")

;(def base "/tmp/")

(defn ssh-url [user repo]
  (str "git@github.com:" user "/" repo ".git"))

(defn user-dir [user]
  (str base user))

(defn repo-dir [user repo]
  (str base user "/" repo))

(defn ensure-user-dir [user]
  (let [dir (user-dir user)]
    (when-not (fs/directory? dir)
      (println "creating user dir: " dir)
      (fs/create-dir dir))))



(defn clone [user repo]
  (assert (fs/directory? base) (str "base dir does not exist: " base))
  (let [dir (user-dir user)
        url (ssh-url user repo)]
    (println "cloning git repo " user ":" repo "from: " url " dir: " dir)
    (ensure-user-dir user)
    (shell {:dir dir} "git clone" url)))

(defn pull [user repo]
  (let [dir (repo-dir user repo)
        url (ssh-url user repo)]
    (assert (fs/directory? base) (str "repo dir does not exist: " dir))  
    (println "pulling git repo " user ":" repo "from: " url " dir: " dir)
    (shell {:dir dir} "git pull")))


(defn clone-or-pull [user repo]
  (let [dir (repo-dir user repo)]
    (if (fs/directory? dir)
      (pull user repo)
      (clone user repo))))


(defn clone-or-pull-user-repos [user repos]
  (doall 
    (map #(git/clone-or-pull user %) repos)))
 
