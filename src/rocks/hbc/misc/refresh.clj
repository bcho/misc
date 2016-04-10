(ns rocks.hbc.misc.refresh
  "Refresh modified ns."
  (:require [clojure.set :refer [union]]
            [ns-tracker.core :refer [ns-tracker]]))

(def ^:private dir-trackers (atom {}))

(defn- dir-tracker
  [dir]
  (let [tracker (or (@dir-trackers dir)
                    (ns-tracker [dir]))]
    (swap! dir-trackers #(conj % {dir tracker}))
    tracker))

(defn- modified
  [dirs]
  (loop [dirs dirs
         rv #{}]
    (if-let [dir (first dirs)]
      (let [tracker (dir-tracker dir)]
        (recur (next dirs) (union rv (set (tracker)))))
      rv)))

(defn refresh
  [& directories]
  (let [to-refresh (modified directories)]
    (doseq [ns-sym to-refresh]
      (require ns-sym :reload))
    (prn (str "reloaded " to-refresh))))
