(ns apex-rpc.core-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [apex-rpc.core :refer [call]]
            [slingshot.slingshot :refer [try+]]
            [ring.adapter.jetty :as ring]))

(defn handler [req]
  (condp = [(:request-method req) (:uri req)]
    [:post "/get_body"]
    {:status 200 :body (:body req)}
    [:post "/get_headers"]
    {:status 200 :body (json/write-str (:headers req))}
    [:post "/get_json_error"]
    {:status 400 :body (json/write-str {:type "type" :message "message"})}
    [:post "/get_unknown_error"]
    {:status 500 :body "o noes"}))

(defn run-server
  []
  (defonce server
    (do
      (future
        (ring/run-jetty handler {:port 18080}))
      (Thread/sleep 1000))))

(deftest ^{:integration true} call-test
  (run-server)
  (testing "Unauthenticated RPC call."
    (let [res (call {:url "http://localhost:18080"
                     :method "get_body"
                     :args-map {:foo "bar"}})]
      (is (= (:foo res) "bar"))))

  (testing "Authenticated RPC call."
    (let [res (call {:url "http://localhost:18080"
                     :method "get_headers"
                     :auth-token "token"
                     :args-map {:foo "bar"}})]
      (is (= (:authorization res) "Bearer token"))))

  (testing "Errors (JSON)"
    (let [res (atom {})]
      (try+
        (call {:url "http://localhost:18080"
               :method "get_json_error"})
        (catch [:status 400] ex
          (reset! res ex)))
      (is (= (:type @res) "type"))
      (is (= (:message @res) "message"))))

  (testing "Errors (plain text)"
    (let [res (atom {})]
      (try+
        (call {:url "http://localhost:18080"
               :method "get_unknown_error"})
        (catch [:status 500] ex
          (reset! res ex)))
      (is (= (:message @res) "o noes")))))
