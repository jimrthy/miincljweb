(ns com.jimrthy.cluster-web.shared.persistence-test
  (:require [clojure.test :refer [deftest is testing] :as test]
            [com.jimrthy.cluster-web.shared.persistence :refer :all]))

(deftest basics
  (testing "How do I want this to work?"
    (is = 0 (dec 1))))
