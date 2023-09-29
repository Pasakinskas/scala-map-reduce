package com.pasakinskas.framework

import com.pasakinskas.FileReader

// reduced should return a map
class NaiveRunner[K, V, R](
  mapReduce: MapReduce[K, V, R],
  fileReader: FileReader,
) {

  def run(): Unit = {
    val mapped = mapEntries()
    val shuffled = shuffleEntries(mapped)

    val reduced = shuffled.map(one => mapReduce.reducer(one))
    reduced.foreach(println)
  }

  private def mapEntries(): Seq[KeyValue[K, V]] = {
    val pairs = for {
      (location, mapper) <- mapReduce.mappers()
      row <- fileReader.getEntries(location)
      result <- mapper(row)
    } yield result

    pairs.toSeq
  }

  private def shuffleEntries(pairs: Iterable[KeyValue[K, V]]): Seq[KeyValue[K, Seq[V]]] = {
    pairs.groupBy(_.key).map({
      case (key, grouped) => KeyValue(key, grouped.map(_.value).toSeq)
    }).toSeq
  }
}
