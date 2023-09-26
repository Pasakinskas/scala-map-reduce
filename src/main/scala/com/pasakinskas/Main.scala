package com.pasakinskas

import com.pasakinskas.framework.KeyValue
import com.pasakinskas.tasks.ClickCounter

object Main {

  def main(args: Array[String]): Unit = {
    val counter = new ClickCounter

    val mapped = for {
      line <- FileReader.getEntries("data/clicks")
      row <- line
    } yield counter.mapper(row)

    val shuffled = mapped.groupBy(_.key).map({
      case (key, grouped) => KeyValue(key, grouped.map(_.value))
    })

    val reduced = shuffled.map(counter.reducer)
    reduced.foreach(println)
  }
}
