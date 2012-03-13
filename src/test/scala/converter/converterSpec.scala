/**
 * Copyright (C) 2011 Osinka <http://osinka.ru>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.osinka.camel.scala
package converter

import org.scalatest.FunSpec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

import reflect.Manifest

@RunWith(classOf[JUnitRunner])
class ConverterSpec extends FunSpec with CamelSpec with MustMatchers {
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
    it("must convert java list -> scala list") {
      to[List[Int]]( Collections.singletonList(2) ) must be('defined)
      to[List[Int]]( Collections.emptyList ) must equal(Some(Nil))
    }
    it("must convert scala list -> java list") {
      to[JList[Int]]( 2 :: Nil) must be('defined)
      to[JList[Int]]( Nil ) must be('defined)
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

  describe("option converter") {
    import collection.{Iterator, Iterable, Map, Seq, Set}
    import java.lang.{Iterable => JIterable}
    import java.util.{Collection => JCollection, Iterator => JIterator, List => JList}

    it("must convert option -> list") {
      to[List[Int]]( Some(2) ) must be('defined)
    }
    it("must convert option -> iterable") {
      to[Iterable[Int]]( Some(2) ) must be('defined)
    }
    it("must convert option -> iterator") {
      to[Iterator[Int]]( Some(2) ) must be('defined)
    }
    it("must convert option -> jlist") {
      to[JList[Int]]( Some(2) ) must be('defined)
    }
    it("must convert option -> jcollection") {
      to[JCollection[Int]]( Some(2) ) must be('defined)
    }
    it("must convert option -> jiterator") {
      to[JIterator[Int]]( Some(2) ) must be('defined)
    }
    it("must convert option -> jiterable") {
      to[JIterable[Int]]( Some(2) ) must be('defined)
    }
  }

  private def to[T](x: AnyRef)(implicit m: Manifest[T]): Option[T] = Option( context.getTypeConverter.mandatoryConvertTo(m.erasure, x).asInstanceOf[T] )
}
