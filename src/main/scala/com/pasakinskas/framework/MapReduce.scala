package com.pasakinskas.framework


trait MapReduce[K, V, R] {

  def mappers(): Map[String, Mapper[K, V]]

  def reducer(input: KeyValue[K, Seq[V]]): Option[R]

  def output: String

  def outputFormat(reduceResult: R): String = {
    reduceResult.toString
  }
}

trait Mapper[K, V] {

  def apply(input: Map[String, String]): Option[KeyValue[K, V]]
}

case class KeyValue[K, V](key: K, value: V)