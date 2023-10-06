package com.pasakinskas.setup

import com.pasakinskas.framework.{KeyValue, Mapper}

class WordCounterMapper extends Mapper[String, Int] {
  override def apply(input: Map[String, String]): Option[KeyValue[String, Int]] =
    Some(KeyValue(input("word"), 1))
}
