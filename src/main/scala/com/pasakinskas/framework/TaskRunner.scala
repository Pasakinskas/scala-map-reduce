package com.pasakinskas.framework

import com.pasakinskas.{LineEntry, TaskedFileReader}
import monix.eval.Task


class TaskRunner[K, V, R](
  mapReduce: MapReduce[K, V, R],
  taskedFileReader: TaskedFileReader,
) {

  def run(): Task[Seq[R]] = {
    for {
      mapResults <- Task.parSequence(getTasksToMap())
      shuffled = shuffleEntries(mapResults.flatten)
      reduced = shuffled.flatMap(mapReduce.reducer)
    } yield reduced
  }

  private def getTasksToMap() = {
    for {
      (one, two) <- mapReduce.mappers()
      rows = taskedFileReader.getEntries(one)
      task <- rows
      mapResult = mapChunk(task, two)
    } yield mapResult
  }

  private def mapChunk(chunk: Task[Seq[LineEntry]], mapper: Mapper[K, V]): Task[Seq[KeyValue[K, V]]] = {
    for {
      one <- chunk
      result = one.map(one => mapper.apply(one.values))
    } yield  result.flatten
  }

  private def shuffleEntries(pairs: Iterable[KeyValue[K, V]]): Seq[KeyValue[K, Seq[V]]] = {
    pairs.groupBy(_.key).map({
      case (key, grouped) => KeyValue(key, grouped.map(_.value).toSeq)
    }).toSeq
  }
}
