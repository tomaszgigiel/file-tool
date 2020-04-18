(ns pl.tomaszgigiel.file-tool.common-test
  (:require [clojure.test :as tst])
  (:require [pl.tomaszgigiel.file-tool.common :as common])
  (:require [pl.tomaszgigiel.file-tool.test-config :as test-config]))

(tst/use-fixtures :once test-config/once-fixture)
(tst/use-fixtures :each test-config/each-fixture)
