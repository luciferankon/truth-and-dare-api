(ns truth-and-dare-api.server
  (:require [ring.adapter.jetty :as jetty]
            [truth-and-dare-api.core :as game]
            [ring.middleware.params :refer :all]
            [ring.middleware.cors :refer :all]
            [clojure.data.json :as json]))

(def game-state (atom {:players []}))

(defn generate-response
  [body]
  {:status  200
   :headers {"Content-Type" "text/plain"}
   :body    body})

(defn get-game []
  (generate-response (json/write-str @game-state)))

(defn play []
  (generate-response (json/write-str (swap! game-state game/play))))

(defn add-player [request]
  (generate-response (json/write-str (swap! game-state #(game/add-player % (keyword (get-in request [:query-params "player"])))))))

(defn add-question [request]
  (generate-response (json/write-str (swap! game-state #(game/add-question % (get-in request [:query-params "question"]))))))

(defn add-answer [request]
  (generate-response (json/write-str (swap! game-state #(game/add-answer % (get-in request [:query-params "answer"]))))))

(defn handler [request]
  (cond
    (= (:uri request) "/game") (get-game)
    (= (:uri request) "/play") (play)
    (= (:uri request) "/add_player") (add-player request)
    (= (:uri request) "/question") (add-question request)
    (= (:uri request) "/answer") (add-answer request)))

(defn -main []
  (jetty/run-jetty (-> handler
                       wrap-params
                       (wrap-cors :access-control-allow-origin [#".*"]
                                  :access-control-allow-methods [:get :put :post :delete])) {:port 3000}))
