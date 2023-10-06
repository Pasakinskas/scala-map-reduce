## MapReduce framework

This small MapReduce example was coded with Scala using [Monix](https://monix.io/)

To run all tests `sbt test`

### Project Structure

    src/main
        - scala/com/pasakinskas - root of the source code
            - examples - MapReduce tasks written with the framework
            - framework - framework implementation
        - resources/data - location of the input data for tasks

    src/test/scala/com/pasakinskas - root of the source code
        - helpers - code classes to align easier to read test input to correct types
        - setup - an example word-counting MapReduce task to use in tests

### Framework

The framework consists of three parts
- FileReaderWriter, deals with IO. 
- MapReduce, an interface for defining MapReduce operations
- TaskRunner, actually does the map, shuffle and reduce operations

### MapReduce

This is the Interface that one must extend to create their own MapReduce tasks.

    trait MapReduce[K, V, R] {

    def mappers(): Map[String, Mapper[K, V]]

    def reducer(input: KeyValue[K, Seq[V]]): Option[R]

    def output: String

    def outputFormat(reduceResult: R): String = {
        reduceResult.toString
        }
    }

K - the key of KeyValue pairs which will be used for grouping
V - the value of KeyValue pairs
R - reduce step result 

mappers - a Map of data location and Mapper implementation key-value pairs. To match the API specified
in the task examples MapReduce can take several Mappers with their own data sets. for example:

    def mappers(): Map[String, Mapper[String, Databag]] = {
        Map(
            "data/clicks" -> new ClicksMapper(),
            "data/users" -> new UsersMapper(),
        )
    }

the Mapper trait returns an option which is useful for filtering data

    trait Mapper[K, V] {
        def apply(input: Map[String, String]): Option[KeyValue[K, V]]
    }
    
    case class KeyValue[K, V](key: K, value: V)



### FileReaderWriter

The class works on the assumption that we're working with csv files that have headers. One line is
converted to a LineEntry object, which is an abstraction for a Map[String, String].
so that input 

```
name,age,id
Bob,30,123
```

would be converted to Map(name -> Bob, age -> 30, id -> 123)

IO works by getting all csv files in a provided directory, getting the csv data headers and a GroupedIterator
(the input is chunked, so that if one file has a lot of lines, it will produce multiple chunks).
This header and a groupedIterator that produces a monix task for every chunk, is my FileContents abstraction.
The chunks of lines are mapped into LineEntries (mentioned above) and returned as Tasks. So the input is a
sequence of Tasks (one for every chunk) and every task contains one chunk of LineEntries.

### TaskRunner

This is the conductor class, it does mapping, shuffling, reducing and optionally output
Mapping and Reducing are done in parallel using monix Tasks, which act like lazy futures
and using Task.parSequence [monix docs](https://monix.io/docs/current/tutorials/parallelism.html)
shuffling is done after gathering all mapping results and reducing again makes use of the parSequence

#### Final thoughts

After playing around with some basic benchmarking, I've noticed that a single thread works best until the input
reaches about 8MB. When the input is 30MB, single threaded takes around 5000ms, and Tasks with 4 threads took around 2600ms
I scaled it up to 60MB and results seemed the same at slightly less than 2x difference.

I have a feeling that more could be done to make IO more performant, perhaps something smarter than a Map could be used for
the line format and given unlimited time I would've loved to investigate monix Observables. Would love any feedback and thoughts. 