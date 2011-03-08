package com.osinka.camel.scala
package converter

import org.apache.camel.Converter
import collection.JavaConversions._

@Converter
object ScalaImmutableCollections {
  import collection.{Iterator, Iterable, Map, Seq, Set}
  import java.lang.{Iterable => JIterable}
  import java.util.{Collection => JCollection, Enumeration => JEnumeration, Iterator => JIterator, List => JList, Map => JMap, Set => JSet}

  @Converter
  def toJavaIterator[T](iterator: Iterator[T]): JIterator[T] = iterator

  @Converter
  def toEnumeration[T](iterator: Iterator[T]): JEnumeration[T] = iterator

  @Converter
  def toScalaIterator[T](iterator: JIterator[T]): Iterator[T] = iterator

  @Converter
  def toScalaIterator[T](enumeration: JEnumeration[T]): Iterator[T] = enumeration

  @Converter
  def toJavaIterable[T](iterable: Iterable[T]): JIterable[T] = asJavaIterable(iterable)

  @Converter
  def toScalaIterable[T](iterable: JIterable[T]): Iterable[T] = iterable

  @Converter
  def toJavaCollection[T](iterable: Iterable[T]): JCollection[T] = iterable

  @Converter
  def toScalaIterable[T](collection: JCollection[T]): Iterable[T] = collection

  @Converter
  def toJavaSet[T](set: Set[T]): JSet[T] = set

  @Converter
  def toJavaMap[A,B](map: Map[A,B]): JMap[A,B] = map
  
  @Converter
  def toJavaList[T](seq: Seq[T]): JList[T] = seq
}

@Converter
object ScalaMutableCollections {
  import collection.mutable.{ConcurrentMap, Buffer, Map, Seq, Set}
  import java.util.{Dictionary => JDictionary, List => JList, Map => JMap, Set => JSet}
  import java.util.concurrent.{ConcurrentMap => JConcurrentMap}

  @Converter
  def toJavaList[T](buffer: Buffer[T]): JList[T] = buffer

  @Converter
  def toScalaBuffer[T](list: JList[T]): Buffer[T] = list

  @Converter
  def toJavaSet[T](set: Set[T]): JSet[T] = set

  @Converter
  def toScalaSet[T](set: JSet[T]): Set[T] = set

  @Converter
  def toJavaDictionary[A,B](map: Map[A,B]): JDictionary[A,B] = map

  @Converter
  def toScalaMap[A,B](dictionary: JDictionary[A,B]): Map[A,B] = dictionary

  @Converter
  def toJavaMap[A,B](map: Map[A,B]): JMap[A,B] = map

  @Converter
  def toScalaMap[A,B](map: JMap[A,B]): Map[A,B] = map

  @Converter
  def toJavaConcurrentMap[A,B](map: ConcurrentMap[A,B]): JConcurrentMap[A,B] = map

  @Converter
  def toScalaConcurrentMap[A,B](map: JConcurrentMap[A,B]): ConcurrentMap[A,B] = map

  @Converter
  def toJavaList[T](seq: Seq[T]): JList[T] = seq
}
