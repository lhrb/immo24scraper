(ns lhrb.immo24scraper
  (:require [postal.core :as postal]
            [clojure.set :as set]
            [clojure.tools.logging :as log]
            [clojure.tools.cli :as cli])
  (:import (org.jsoup Jsoup)
  (org.jsoup.select Elements)
  (org.jsoup.nodes Element))
  (:gen-class))

(defn send-mail [{email :email pw :pw} expose]
  (postal/send-message {:host "smtp.gmail.com"
                        :ssl true
                        :user email
                        :pass pw}

                       {:from email
                        :to email
                        :subject "Neue Wohnung"
                        :body (apply str (for [[_ v] expose] (str v "\n")))}))

(defn get-page [url]
  (.get (Jsoup/connect url)))

(defn get-elems [page css]
  (.select page css))

(defn get-obj-ids
  [url]
  (try
    (let [html (get-page url)
         elems (get-elems html "#resultListItems > li")]
     (->> elems
          (map #(.attr % "data-id"))
          (filter #(seq %))
          set))
    (catch Exception e (log/warn e))))

(defn get-expose
  [url]
  (try
    (let [html (get-page url)
         title (.text (get-elems html "#expose-title"))
         address (.text (get-elems html ".address-block"))
         criteria (.text (get-elems html ".main-criteria-container"))]
      {:title title :address address :criteria criteria :url url})
    (catch Exception e (log/warn e))))

(defn output-mail [mail-fn obj-ids]
  (doseq [url (map #(str "https://www.immobilienscout24.de/expose/" %) obj-ids)]
    (future (mail-fn (get-expose url)))))

(def cli-options
  [["-i" "--interval"
    :default 300000
    :parse-fn #(Integer/parseInt %)]])

(defn -main
  "I don't do a whole lot."
  [& args]
  (let [{:keys [options arguments _ _]} (cli/parse-opts args cli-options)
        interval (:interval options)
        credentials (-> "resources/credentials.edn"
                     slurp
                     clojure.edn/read-string)
        mail-fn (partial send-mail credentials)
        url (first arguments)]

    (log/info (str "Program started with URL: " url
                   " interval: " interval))

    (try
     (loop [coll (get-obj-ids url)]
       (Thread/sleep interval)
       (let [objs (get-obj-ids url)
             not-seen (set/difference objs coll)]

         (log/info "New objects found:" not-seen)

         (output-mail mail-fn not-seen)
         (recur (set/union coll not-seen))))

     (catch Exception e (log/error e))
     (finally (System/exit 1)))))
