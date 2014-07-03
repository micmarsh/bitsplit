(ns bitsplit.utils.thread)

(defmacro thread-loop [& body]
    `(.start (Thread. 
        (fn [] 
            (while true
                ~@body)))))

(defn thread-sleep [minutes]
    (Thread/sleep (* minutes 1000 60)))

(defmacro thread-interval [minutes & body]
    `(thread-loop
        (thread-sleep ~minutes)
        ~@body))