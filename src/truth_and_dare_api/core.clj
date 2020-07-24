(ns truth-and-dare-api.core
  (:gen-class))

(defn play [game]
  (let [players (:players game)
        turn (when (not-empty players)
               (rand-nth players))
        other-players (filter (partial not= turn) players)]
    (assoc game :turn turn :questioner (when (not-empty other-players)
                                         rand-nth other-players))))

(defn add-player [game player]
  (update game :players #(conj % player)))



