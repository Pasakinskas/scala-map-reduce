package com.pasakinskas.examples.two

import com.pasakinskas.framework.{KeyValue, Mapper}

import scala.collection.mutable

class UsersMapper extends Mapper[String, Databag] {
  override def apply(input: Map[String, String]): Option[KeyValue[String, Databag]] = {
    if (input.getOrElse("country", "") != "LT") {
      None
    } else {
      val userId = input("id")
      val fields = mutable.Map[String, String]()

      fields.addAll(input)
      fields.addOne("table" -> "users")

      Some(KeyValue(userId, Databag(fields.toMap)))
    }
  }
}
class ClicksMapper extends Mapper[String, Databag] {
  override def apply(input: Map[String, String]): Option[KeyValue[String, Databag]] = {
    val userId = input("user_id")
    val fields = mutable.Map[String, String]()
      .addAll(input)
      .addOne("table" -> "clicks")

    Some(KeyValue(userId, Databag(fields.toMap)))
  }
}
