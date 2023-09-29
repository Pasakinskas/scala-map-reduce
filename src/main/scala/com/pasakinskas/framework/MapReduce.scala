package com.pasakinskas.framework


trait MapReduce[K, V, R] {

  def mappers(): Map[String, Mapper[K, V]]

  def reducer(input: KeyValue[K, Seq[V]]): KeyValue[K, R]
}

trait Mapper[K, V] {

  def apply(input: Seq[Map[String, String]]): Seq[KeyValue[K, V]]
}

case class KeyValue[K, V](key: K, value: V)