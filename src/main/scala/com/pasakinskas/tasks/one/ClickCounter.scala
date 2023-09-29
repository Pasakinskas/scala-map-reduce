package com.pasakinskas.tasks.one

import com.pasakinskas.framework.{KeyValue, MapReduce, Mapper}

class ClickCounter extends MapReduce[String, Click, Int] {

  def mappers(): Map[String, Mapper[String, Click]] = {
    Map(
      "data/clicks" -> new ClicksMapper(),
    )
  }

  override def reducer(input: KeyValue[String, Seq[Click]]): KeyValue[String, Int] = {
    KeyValue(input.key, input.value.size)
  }
}

class ClicksMapper extends Mapper[String, Click] {
  override def apply(input: Seq[Map[String, String]]): Seq[KeyValue[String, Click]] = {
    input.map(row => {
      val date = row("date")
      val userId = row("user_id")
      val location = row("click_target")

      KeyValue(date, Click(date, userId, location))
    })
  }
}

case class Click(date: String, userId: String, location: String)
