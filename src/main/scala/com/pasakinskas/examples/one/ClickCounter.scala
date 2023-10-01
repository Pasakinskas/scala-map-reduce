package com.pasakinskas.examples.one

import com.pasakinskas.framework.{KeyValue, MapReduce, Mapper}

class ClickCounter extends MapReduce[String, Click, (String, Int)] {

  def mappers(): Map[String, Mapper[String, Click]] = {
    Map(
      "data/clicks" -> new ClicksMapper(),
    )
  }

  override def reducer(input: KeyValue[String, Seq[Click]]): Option[(String, Int)] = {
    Some((input.key, input.value.size))
  }
}

class ClicksMapper extends Mapper[String, Click] {

  override def apply(input: Map[String, String]): Option[KeyValue[String, Click]] = {
    val date = input("date")
    val userId = input("user_id")
    val location = input("click_target")

    Some(KeyValue(date, Click(date, userId, location)))
  }
}

case class Click(date: String, userId: String, location: String)
