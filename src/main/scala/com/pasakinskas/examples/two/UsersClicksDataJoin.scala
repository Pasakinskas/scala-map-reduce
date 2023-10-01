package com.pasakinskas.examples.two

import com.pasakinskas.framework.{KeyValue, MapReduce, Mapper}

class UsersClicksDataJoin extends MapReduce[String, Databag, FilteredClicksPerUser] {

  def mappers(): Map[String, Mapper[String, Databag]] = {
    Map(
      "data/clicks" -> new ClicksMapper(),
      "data/users" -> new UsersMapper(),
    )
  }

  override def reducer(input: KeyValue[String, Seq[Databag]]): Option[FilteredClicksPerUser] = {
    val user = input.value.find(_.fields.getOrElse("table", "") == "users")
    val userFields = user.map(_.fields).getOrElse(Map.empty)

    val joined = input.value
      .map(_.fields)
      .filter(_.getOrElse("table", "") == "clicks")
      .map(kv => kv ++ userFields)
      .filter(_.contains("country"))

    if (joined.nonEmpty) Some(FilteredClicksPerUser(joined)) else None
  }

  override def outputFormat(joinResult: FilteredClicksPerUser): String = {
    val formattedRows = for {
      row <- joinResult.rows
      pair = row.map(kv => s"${kv._1}:${kv._2}").mkString(",")
    } yield pair

    formattedRows.mkString("\n")
  }

  override def output: String = "data/filtered-clicks.csv"
}

case class FilteredClicksPerUser(rows: Seq[Map[String, String]])

case class Databag(fields: Map[String, String])