(ns drain.bot
  (:require [ring.adapter.jetty :as jetty]
            [irclj.core :as irc]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(defn join [joined-channels channel irc]
  (if (joined-channels channel)
    joined-channels
    (do (irc/join irc (str "#" channel))
        (conj joined-channels channel))))

(defn trim [s] (string/join " " (drop 7 (string/split s #" "))))

(defn parse [{:keys [uri body]} irc channels]
  (if-let [channel (second (re-find #"^/([^/]+)" uri))]
    (let [pattern (re-pattern (or (re-find #"^/[^/]+/([^/]+)" uri) "."))]
      (swap! channels join channel irc)
      [(str "#" channel) (->> (line-seq (io/reader body))
                              (filter (partial re-find pattern))
                              (map trim) (apply str))])
    (throw (ex-info "channel not found" {:status 404}))))

(defn app [irc channels req]
  (if (= :post (:request-method req))
    (try
      (let [[channel msg] (parse req irc channels)]
        (irc/message irc channel msg)
        {:status 200
         :headers {"Content-Type" "text/plain"}
         :body "OK"})
      (catch Exception e
        (.printStackTrace e)
        {:status (:status (ex-data e) 500)
         :headers {"Content-Type" "text/plain"}
         :body (.getMessage e)}))
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body "This is drainbot."}))

(defn -main []
  (let [port (Integer. (or (System/getenv "PORT") 5000))
        nick (or (System/getenv "NICK") "drainbot")
        irc (irc/connect "irc.freenode.net" 6667 nick)]
    (jetty/run-jetty (partial #'app irc (atom #{})) {:port port :join? false})))

;; (def s (-main))
