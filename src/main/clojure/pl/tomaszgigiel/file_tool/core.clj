(ns pl.tomaszgigiel.file-tool.core
  (:require [clojure.edn :as edn])
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str])
  (:require [clojure.tools.logging :as log])
  (:require [pl.tomaszgigiel.file-tool.cmd :as cmd])
  (:require [pl.tomaszgigiel.file-tool.common :as common])
  (:gen-class))

(defn- file-tool-replace? [x] (some? (:file-tool.replace.input-path x)))
(defn- file-tool-distinct-lines? [x] (some? (:file-tool.distinct-lines.input-path x)))
(defn- file-tool-dedupe-lines? [x] (some? (:file-tool.dedupe-lines.input-path x)))
(defn- file-tool-sort-lines? [x] (some? (:file-tool.sort-lines.input-path x)))
(defn- file-tool-map-lines? [x] (some? (:file-tool.map-lines.input-path x)))
(defn- file-tool-merge-lines? [x] (some? (:file-tool.merge-lines.input-path.left x)))

(defn- file-tool-replace [x]
  (let [input-path (:file-tool.replace.input-path x)
        output-path (:file-tool.replace.output-path x)
        replacements (:file-tool.replace.replacements x)]
    (do (log/info "file-tool.replace.input-path:" input-path)
      (log/info "file-tool.replace.output-path:" output-path)
      (log/info "file-tool.replace.replacements:" replacements)

      (spit output-path (reduce #(str/replace %1 (re-pattern (:old %2)) (:new %2)) (slurp input-path) replacements)))))

(defn- file-tool-distinct-lines [x]
  (let [input-path (:file-tool.distinct-lines.input-path x)
        output-path (:file-tool.distinct-lines.output-path x)]
    (do (log/info "file-tool.distinct-lines.input-path:" input-path)
      (log/info "file-tool.distinct-lines.output-path:" output-path)

      (with-open [r (io/reader input-path)
                  w (io/writer output-path)]
        (doall (map #(.write w (str % "\r\n")) (distinct (line-seq r))))))))

(defn- file-tool-dedupe-lines [x]
  (let [input-path (:file-tool.dedupe-lines.input-path x)
        output-path (:file-tool.dedupe-lines.output-path x)]
    (do (log/info "file-tool.dedupe-lines.input-path:" input-path)
      (log/info "file-tool.dedupe-lines.output-path:" output-path)

      (with-open [r (io/reader input-path)
                  w (io/writer output-path)]
        (doall (map #(.write w (str % "\r\n")) (dedupe (line-seq r))))))))

(defn- file-tool-sort-lines [x]
  (let [input-path (:file-tool.sort-lines.input-path x)
        output-path (:file-tool.sort-lines.output-path x)]
    (do (log/info "file-tool.sort-lines.input-path:" input-path)
      (log/info "file-tool.sort-lines.output-path:" output-path)

      (with-open [r (io/reader input-path)
                  w (io/writer output-path)]
        (doall (map #(.write w (str % "\r\n")) (sort (line-seq r))))))))

(defn- file-tool-map-lines [x]
  (let [input-path (:file-tool.map-lines.input-path x)
        output-path (:file-tool.map-lines.output-path x)
        map-lines-map (:file-tool.map-lines.map x)]
    (do (log/info "file-tool.map-lines.input-path:" input-path)
      (log/info "file-tool.map-lines.output-path:" output-path)
      (log/info "file-tool.map-lines.map:" map-lines-map)

      (with-open [r (io/reader input-path)
                  w (io/writer output-path)]
        (doall (map #(.write w (str ((eval (read-string map-lines-map)) %) "\r\n")) (line-seq r)))))))

; https://stackoverflow.com/questions/18940629/using-map-with-different-sized-collections-in-clojure
(defn map-longest [f default & colls]
  (lazy-seq
    (when (some seq colls)
      (cons
        (apply f (map #(if (seq %) (first %) default) colls))
        (apply map-longest f default (map rest colls))))))

(defn- file-tool-merge-lines [x]
  (let [input-path-left (:file-tool.merge-lines.input-path.left x)
        input-path-right (:file-tool.merge-lines.input-path.right x)
        output-path (:file-tool.merge-lines.output-path x)
        merge-lines-separator (:file-tool.merge-lines.separator x)]
    (do (log/info "file-tool.merge-lines.input-path.left:" input-path-left)
      (log/info "file-tool.merge-lines.input-path.right:" input-path-right)
      (log/info "file-tool.merge-lines.output-path:" output-path)
      (log/info "file-tool.merge-lines.separator:" merge-lines-separator)

      (with-open [r-left (io/reader input-path-left)
                  r-right (io/reader input-path-right)
                  w (io/writer output-path)]
        (doall (map-longest #(.write w (str %1 merge-lines-separator %2 "\r\n")) "?" (line-seq r-left) (line-seq r-right)))))))

(defn- work [x]
  (cond
    (file-tool-replace? x) (file-tool-replace x)
    (file-tool-distinct-lines? x) (file-tool-distinct-lines x)
    (file-tool-dedupe-lines? x) (file-tool-dedupe-lines x)
    (file-tool-sort-lines? x) (file-tool-sort-lines x)
    (file-tool-map-lines? x) (file-tool-map-lines x)
    (file-tool-merge-lines? x) (file-tool-merge-lines x)))

(defn- file-tool [path]
  (let [props (with-open [r (io/reader path)] (edn/read (java.io.PushbackReader. r)))]
    (doall (map work props))))

(defn -main [& args]
  "file-tool: file tool"
    (let [{:keys [uri options exit-message ok?]} (cmd/validate-args args)]
      (if exit-message
        (cmd/exit (if ok? 0 1) exit-message)
        (file-tool (first args)))
      (log/info "ok")
      (shutdown-agents)))
