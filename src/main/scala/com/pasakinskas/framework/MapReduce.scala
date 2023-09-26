package com.pasakinskas.framework


trait MapReduce[K, V, R] {

  def mapper(input: Map[String, String]): KeyValue[K, V]

  def reducer(input: KeyValue[K, Seq[V]]): KeyValue[K, R]
}

case class KeyValue[K, V](key: K, value: V)