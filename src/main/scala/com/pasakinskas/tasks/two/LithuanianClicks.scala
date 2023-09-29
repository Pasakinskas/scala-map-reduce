package com.pasakinskas.tasks.two

import com.pasakinskas.framework.{KeyValue, MapReduce, Mapper}

import scala.collection.mutable

class LithuanianClicks extends MapReduce[String, Databag, Seq[Map[String, String]]] {

  def mappers(): Map[String, Mapper[String, Databag]] = {
    Map(
      "data/clicks" -> new ClicksMapper(),
      "data/users" -> new UsersMapper(),
    )
  }

  override def reducer(input: KeyValue[String, Seq[Databag]]): Option[Seq[Map[String, String]]] = {
    val user = input.value.find(_.fields.getOrElse("table", "") == "users")
    val userFields = user.map(_.fields).getOrElse(Map.empty)

    val entries = input.value
      .map(_.fields)
      .filter(_.getOrElse("table", "") == "clicks")
      .map(_.addAll(userFields))
      .filter(_.contains("country"))
      .map(_.toMap)

    if (entries.nonEmpty) Some(entries) else None
  }
}

class UsersMapper extends Mapper[String, Databag] {
  override def apply(input: Seq[Map[String, String]]): Seq[KeyValue[String, Databag]] = {
    input
      .filter(row => row("country") == "LT")
      .map(row => {
        val userId = row("id")
        val fields = mutable.Map[String, String]()

        fields.addAll(row)
        fields.addOne("table" -> "users")

        KeyValue(userId, Databag(fields))
      })
  }
}
class ClicksMapper extends Mapper[String, Databag] {
  override def apply(input: Seq[Map[String, String]]): Seq[KeyValue[String, Databag]] = {
    input
      .map(row => {
        val userId = row("user_id")
        val fields = mutable.Map[String, String]()
          .addAll(row)
          .addOne("table" -> "clicks")

        KeyValue(userId, Databag(fields))
      })
  }
}

case class Databag(fields: mutable.Map[String, String])