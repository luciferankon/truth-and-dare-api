(ns truth-and-dare-api.server
  (:require [ring.adapter.jetty :as jetty]
            [truth-and-dare-api.core :as game]
            [ring.middleware.params :refer :all]))

(def game-state (atom {:players []}))

(defn generate-response
  [body]
  {:status  200
   :headers {"Content-Type" "text/plain"}
   :body    body})

(def create-game
  (generate-response (pr-str @game-state)))

(def play
  (generate-response (pr-str (swap! game-state game/play))))

(defn add-player [request]
  (generate-response (pr-str (swap! game-state #(game/add-player % (keyword (get-in request [:query-params "player"])))))))

(defn handler [request]
  (cond
    (= (:uri request) "/create_game") create-game
    (= (:uri request) "/play") play
    (= (:uri request) "/add_player") (add-player request)))

(defn -main []
  (jetty/run-jetty (wrap-params handler) {:port 3000}))
