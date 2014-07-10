(ns bitsplit.utils.thread)

#+clj
(defmacro thread-loop [& body]
    `(.start (Thread. 
        (fn [] 
            (while true
                ~@body)))))
#+clj
(defn thread-sleep [minutes]
    (Thread/sleep (* minutes 1000 60)))

#+clj
(defmacro thread-interval [minutes & body]
    `(thread-loop
        (thread-sleep ~minutes)
        ~@body))