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
    reduced
      .filter(_.nonEmpty)
      .foreach(println)
  }

  private def mapEntries(): Seq[KeyValue[K, V]] = {
    val pairs = for {
      (location, mapper) <- mapReduce.mappers()
      row <- fileReader.getEntries(location)
      line <- row
    } yield mapper(line)

    pairs.filter(_.nonEmpty).map(_.get).toSeq
  }

  private def shuffleEntries(pairs: Iterable[KeyValue[K, V]]): Seq[KeyValue[K, Seq[V]]] = {
    pairs.groupBy(_.key).map({
      case (key, grouped) => KeyValue(key, grouped.map(_.value).toSeq)
    }).toSeq
  }
}
