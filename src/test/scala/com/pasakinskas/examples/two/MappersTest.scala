package com.pasakinskas.examples.two

import com.pasakinskas.framework.KeyValue
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class MappersTest extends Specification {
  "UsersMapper.apply" should {
    "return none if the country is not LT" in new Context {
      val input = Map("country" -> "PL", "id" -> "123", "field" -> "value")
      usersMapper(input) must beNone
    }

    "return a KeyValue with all required fields" in new Context {
      val input = Map("country" -> "LT", "id" -> "123", "field" -> "value")
      val expected = KeyValue("123", Databag(input ++ Map("table" -> "users")))

        usersMapper(input) must beSome(expected)
    }
  }

  "ClicksMapper.apply" should {
    "return a KeyValue with all required fields" in new Context {
      val input = Map("user_id" -> "123", "some-field" -> "some-value")
      val expected = KeyValue("123", Databag(input ++ Map("table" -> "clicks")))

      clicksMapper(input) must beSome(expected)
    }
  }
}

class Context extends Scope {
  val usersMapper = new UsersMapper
  val clicksMapper = new ClicksMapper
}
