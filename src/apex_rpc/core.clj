(ns apex-rpc.core
  (:require [clojure.data.json :as json]
            [clj-http.lite.client :as client]
            [slingshot.slingshot :refer [try+ throw+]]))

(defn call
  "Call method with params via a POST request."
  [{:keys [url method auth-token args-map]}]
  {:pre [(every? string? [url method])]}
  (let [uri (str url "/" method)
        headers (cond-> {}
                  auth-token (assoc "Authorization" (str "Bearer " auth-token)))
        body (json/write-str (or args-map {}))
        res (client/post uri
                         {:headers          headers
                          :content-type     :json
                          :accept           :json
                          :body             body
                          :throw-exceptions false})
        status (:status res)]
    (if (>= status 300)
      ;; we have an error, try to parse a well-formed json
      ;; error response, otherwise default to status code
      (let [e (try+
                (let [{:keys [type message]} (json/read-str (:body res)
                                                            :key-fn keyword)]
                  {:status  status
                   :type    type
                   :message message})
                (catch Exception ex
                  {:status  status
                   :message (:body res)}))]
        (throw+ e))
      (json/read-str (:body res)
                     :key-fn keyword))))
