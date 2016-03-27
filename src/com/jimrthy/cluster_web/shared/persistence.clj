(ns com.jimrthy.cluster-web.shared.persistence
  "Honestly, I shouldn't inflict a database layer on any other poor souls who might choose to use this.

Just declare a protocol and let that be fulfilled as needed.

But, really, these considerations are the subject of a different branch.

For now, I need to stay focused on digging components out of the stone age of a year or two ago"
  (:require [datomic.api :as d]))
