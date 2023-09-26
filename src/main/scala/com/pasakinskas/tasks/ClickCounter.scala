package com.pasakinskas.tasks

import com.pasakinskas.framework.{KeyValue, MapReduce}

class ClickCounter extends MapReduce[String, Click, Int] {

  override def mapper(input: Map[String, String]): KeyValue[String, Click] = {
    val date = input("date")
    val userId = input("user_id")
    val location = input("click_target")

    KeyValue(date, Click(date, userId, location))
  }

  override def reducer(input: KeyValue[String, Seq[Click]]): KeyValue[String, Int] = {
    KeyValue(input.key, input.value.size)
  }
}

case class Click(date: String, userId: String, location: String)
