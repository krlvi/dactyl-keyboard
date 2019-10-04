(ns y-and-b)

(def finger (component key-place
                       (->> (grid -1 6 0 5 :key-1u)
                            (knock-out-at 0 4)
                            (knock-out-at 0 5)
                            (column-glue-joint-between -1 0)
                            (column-glue-joint-between 1 2)
                            (column-glue-joint-between 3 4))))

(def thumb (component thumb-place
                      (->> empty
                           (add-key 0 -1/2 :key-2u-vertical)
                           (add-key 1 -1/2 :key-2u-vertical)
                           (add-key 2 -1 :key-1u)
                           (add-key 2 0 :key-1u)
                           (add-key 2 1 :key-1u))))

(def layout (->> (union finger thumb)
                 (
                    
(deflayout the5x6
  (component finger key-place
             (->> (grid 0 6 
