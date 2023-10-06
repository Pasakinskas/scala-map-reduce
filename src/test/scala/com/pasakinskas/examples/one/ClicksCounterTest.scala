package com.pasakinskas.examples.one

import com.pasakinskas.framework.KeyValue
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class ClicksCounterTest extends Specification {

  "ClicksMapper.apply" should {
    "maps input to KeyValue" in new Context {
      val date = "2023-01-01"
      val userId = "1"
      val clickTarget = "ad"

      val keyValue = KeyValue(date, Click(date, userId, clickTarget))
      val mapResult = mapper(Map("date" -> date, "user_id" -> userId, "click_target" -> clickTarget))

      mapResult must beSome(keyValue)
    }
  }

  "ClicksCounter.reducer" should {
    "sum the number of clicks per day" in new Context {
      val date = "2023-01-01"
      val groupedClicks = Seq(Click(date, "1", "ad"), Click(date, "2", "profile"))
      val input = KeyValue(date, groupedClicks)

        counter.reducer(input) must beSome (date, groupedClicks.size)
    }
  }
}

class Context extends Scope {
  val counter = new ClickCounter
  val mapper = new ClicksMapper
}
