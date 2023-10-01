package com.pasakinskas.examples.one

import com.pasakinskas.framework.KeyValue
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class ClicksCounterTest extends Specification {

  "ClicksMapper" should {
    "maps input to KeyValue" in new Context {
      val date = "2023-01-01"
      val userId = "1"
      val clickTarget = "ad"

      val keyValue = KeyValue(date, Click(date, userId, clickTarget))
      val mapResult = mapper(Map("date" -> date, "user_id" -> userId, "click_target" -> clickTarget))

      mapResult must beSome(keyValue)
    }
  }
}

class Context extends Scope {
  val mapper = new ClicksMapper()
}
