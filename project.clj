(defproject file-tool "1.0.0.0-SNAPSHOT"
  :description "file-tool: file tool"
  :url "http://tomaszgigiel.pl"
  :license {:name "Apache License"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.logging "0.5.0"]]

  :source-paths ["src/main/clojure"]
  :test-paths ["src/test/clojure" "src/main/clojure"]
  :resource-paths ["src/main/resources"]
  :target-path "target/%s"
  :jar-name "file-tool.jar"
  :uberjar-name "file-tool-uberjar.jar"
  :main pl.tomaszgigiel.file-tool.core
  :aot [pl.tomaszgigiel.file-tool.core]
  :profiles {:test {:resource-paths ["src/test/resources"]}}
)
