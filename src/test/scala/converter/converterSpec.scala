package com.osinka.camel.scala
package converter

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

import reflect.Manifest

@RunWith(classOf[JUnitRunner])
class ConverterSpec extends Spec with CamelSpec with MustMatchers {
  describe("types converter") {
    it("must convert symbol to string") {
      to[String]('Test) must equal(Some("Test"))
    }
    it("must convert string to symbol") {
      to[Symbol]("Test") must equal(Some('Test))
    }
  }

  describe("immutable collections converter") {
    import collection.{Iterator, Iterable, Map, Seq, Set}
    import java.lang.{Iterable => JIterable}
    import java.util.{Collection => JCollection, Collections, Iterator => JIterator, Enumeration, List => JList, Map => JMap, Set => JSet}

    it("must convert scala iterators") {
      val it = Iterator.single(1)
      to[JIterator[Int]](it) must be('defined)
      to[Enumeration[Int]](it) must be('defined)
    }
    it("must convert java iterators") {
      val l = Collections.singletonList(1)
      to[Iterator[Int]](l.iterator) must be('defined)
      to[Iterator[Int]](Collections.enumeration(l)) must be('defined)
    }
    it("must convert scala iterable") {
      val it = Iterable(1)
      to[JIterable[Int]](it) must be('defined)
      to[JCollection[Int]](it) must be('defined)
    }
    it("must convert java collection") {
      val it = Collections.singletonList(1)
      to[Iterable[Int]](it) must be('defined)
    }
    it("must convert scala set") {
      val s = Set(1)
      to[JSet[Int]](s) must be('defined)
    }
    it("must convert java set") {
      val s = Collections.singleton(1)
      to[Set[Int]](s) must (be('defined) and equal(Some(Set(1))))
    }
    it("must convert scala seq") {
      val s = Seq(1)
      to[JList[Int]](s) must be('defined)
    }
    it("must convert scala map") {
      val m = Map("a" -> 1)
      to[JMap[String, Int]](m) must be('defined)
    }
    it("must convert java map") {
      val m = Collections.singletonMap("a", 1)
      to[Map[String, Int]](m) must (be('defined) and equal(Some(Map("a" -> 1))))
    }
  }

  describe("mustable collections converter") {
    import collection.mutable.{ConcurrentMap, Buffer, Map, Seq, Set}
    import java.util.{Dictionary => JDictionary, Hashtable => JHashtable, Collections, List => JList, Map => JMap, Set => JSet}
    import java.util.concurrent.{ConcurrentHashMap => JConcurrentHashMap}

    it("must convert scala buffer") {
      val b = Buffer(1)
      to[JList[Int]](b) must be('defined)
    }
    it("must convert java buffer") {
      val b = Collections.singletonList(1)
      to[Buffer[Int]](b) must be('defined)
    }
    it("must convert scala set") {
      val s = Set(1)
      to[JSet[Int]](s) must be('defined)
    }
    it("must convert java set") {
      val s = Collections.singleton(1)
      to[Set[Int]](s) must be('defined)
    }
    it("must convert scala map") {
      val m = Map("a" -> 1)
      to[JDictionary[String,Int]](m) must be('defined)
      to[JMap[String,Int]](m) must be('defined)
    }
    it("must convert java map") {
      val m = Collections.singletonMap("a", 1)
      to[Map[String,Int]](m) must be('defined)
    }
    it("must convert java dictionary") {
      val d = new JHashtable[String,Int](Collections.singletonMap("a", 1))
      to[Map[String,Int]](d) must be('defined)
    }
    it("must convert java concurrent map") {
      val m = new JConcurrentHashMap[String,Int](Collections.singletonMap("a", 1))
      to[ConcurrentMap[String,Int]](m) must be('defined)
    }
    it("must convert scala seq") {
      val s = Seq(1)
      to[JList[Int]](s) must be('defined)
    }
  }

  private def to[T](x: AnyRef)(implicit m: Manifest[T]): Option[T] = Option( context.getTypeConverter.mandatoryConvertTo(m.erasure, x).asInstanceOf[T] )
}