(ns miincljweb.util
  (:gen-class))

(defn now
  "According to SO, this is the idiomatic way to get the current time."
  []
  (java.util.Date.))

(defn log
  "Persist o for future reference.
  Really should do something more involved."
  [o]
  (let* [now (now)
         msg (format "%tH:%tM:%tS.%tL - %s%n" now now now now (str o))]
    (spit "event.log" msg :append true)
    msg))

;;; Do some brain-dead XML processing
(defn extract-from-node
  "This is brutal and inefficient...aside from being incorrect.
  Multiple instances of the same key will hide each other"
  [key node]
  (first (for [child node]
           (let [attributes (child :attrs)]
             (if (child key)
               (child key)
               (extract-from-node key (child :content)))))))

(defn extract-named-node
  "Note that this does no descent
That comment seems like a lie"
  [node]
  (loop [car (first node) cdr (rest node)]
    (when car
      (if (= :Named_Node (:tag car))
        (:attrs car)
        (recur (first cdr) (rest cdr))))))
