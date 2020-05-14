(ns assignments.benson.lesson-8-answers
  (:require [clojure.core.async :as a
             :refer [>! <! >!! <!! go chan alts!!]]))


(def bing "https://www.bing.com?q=")

(def yippy "https://www.yippy.com/search?query=")

(def yahoo "https://au.search.yahoo.com/search?p=")


(defn search [engine search-term c]
  (go (>! c (slurp (str engine search-term)))))

(defn search-exercise-1
  [search-term]
  (let [s1 (chan)
        s2 (chan)]
    (search "https://www.bing.com/search?q=" search-term s1)
    (search "https://au.search.yahoo.com/search?p=" search-term s2)
    (alts!! [s1 s2])))


(defn search-exercise-2 [search-term & engines]
  (let [result (chan)]
    (doseq [engine engines] (search engine search-term result))
    (<!! result)))

(defn parse-results
  "Parses all links from a vector of SEARCH-RESULTS
   Note: only the first 5 links are taken from each search result for
   ease of testing and validation purposes"
  [search-results]
  (map #(take 5 (re-seq #"https?://[^\"]*" %)) search-results))

;; Should I be using chans for this one aswell?
(defn search-exercise-3
  "Returns a vector of links from each search result"
  [search-term & engines]
  (parse-results (pmap #(slurp (str % search-term)) engines)))
