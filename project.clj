(defproject hden/apex-rpc "0.1.0-SNAPSHOT"
  :description "Simple RPC style APIs with generated clients"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "1.0.0"]
                 [org.martinklepsch/clj-http-lite "0.4.3"]]
  :repl-options {:init-ns apex-rpc.core}
  :plugins [[lein-cloverage "1.2.0"]]
  :profiles
  {:dev {:dependencies [[ring/ring-jetty-adapter "1.3.2"]
                        [ring/ring-devel "1.3.2"]]}})
