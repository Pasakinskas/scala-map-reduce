package com.pasakinskas.framework

import monix.eval.Task

class TaskRunner[K, V, R](
  mapReduce: MapReduce[K, V, R],
  taskedFileReader: FileReader,
) {

  def run(): Task[Seq[R]] = {
    for {
      mapResults <- Task.parSequence(tasksToMap())
      shuffled = shuffleEntries(mapResults.flatten)
      reduced = shuffled.flatMap(mapReduce.reducer)
    } yield reduced
  }

  private def tasksToMap(): Iterable[Task[Seq[KeyValue[K, V]]]] = {
    for {
      (location, mapper) <- mapReduce.mappers()
      tasksWithRows = taskedFileReader.getEntries(location)
      task <- tasksWithRows
      mapResult = mapChunk(task, mapper)
    } yield mapResult
  }

  private def mapChunk(task: Task[Seq[LineEntry]], mapper: Mapper[K, V]): Task[Seq[KeyValue[K, V]]] = {
    for {
      chunk <- task
      result = chunk.flatMap(line => mapper.apply(line.values))
    } yield  result
  }

  private def shuffleEntries(pairs: Iterable[KeyValue[K, V]]): Seq[KeyValue[K, Seq[V]]] = {
    pairs.groupBy(_.key).map({
      case (key, grouped) => KeyValue(key, grouped.map(_.value).toSeq)
    }).toSeq
  }
}
