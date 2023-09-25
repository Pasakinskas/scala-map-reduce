package com.pasakinskas.framework

trait MapReduce[T, K, V] {

  def mapper(input: T): KeyValue[K, V]
}

case class KeyValue[K, V](key: K, Value: V)