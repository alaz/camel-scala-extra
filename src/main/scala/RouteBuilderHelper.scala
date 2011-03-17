/**
 * Copyright (C) 2011 Alexander Azarov <azarov@osinka.com>
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

import reflect.Manifest
import org.apache.camel.{Exchange, Message, Processor, CamelExecutionException}
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.scala.{Preamble, RichExchange, RichMessage}
import org.apache.camel.scala.dsl.{ScalaProcessor, ScalaPredicate}

/**
 * Mix-in into RouteBuilder
 */
trait RouteBuilderHelper extends Preamble { self: RouteBuilder =>
  /**
   * Preamble in Camel 2.6.0 already lifts:
   * - Int (to Period / Frequency);
   * - Exchange to RichExchange
   */

  implicit def enrichMessage(msg: Message) = new RichMessage(msg)

  implicit def enrichUnitF(f: Exchange => Unit) = new ScalaProcessor(f)
  implicit def enrichFn(f: Exchange => Any) = new ScalaPredicate(f)

  /**
   * process { in[String] { _+"11" } toIn }
   * process { in[Int] { 11+ } toOut }
   */
  def in[T](f: (T) => Any)(implicit m: Manifest[T]) = new FnProcessor(exchange => f(exchange.in[T]))

  /**
   * process { out { (s: String) => s+"11" } toIn }
   * process { out[Int] { _+11 } toOut }
   */
  def out[T](f: (T) => Any)(implicit m: Manifest[T]) = new FnProcessor(exchange => f(exchange.getOut.getBody(m.erasure).asInstanceOf[T]))

  /**
   * filter { in[Int] { _ % 2 == 0 } }
   * filter { out { (s: String) => s.startsWith("aa") } }
   */
  implicit def wrapperFilter(w: FnProcessor): ScalaPredicate =
    (exchange: Exchange) => w.f(exchange)

  class FnProcessor(val f: (Exchange) => Any) extends Processor {
    lazy val toOut: ScalaProcessor =
      (exchange: Exchange) => exchange.out = f(exchange)

    lazy val toIn: ScalaProcessor =
      (exchange: Exchange) => exchange.in = f(exchange)

    override def process(exchange: Exchange) {
      toIn.process(exchange)
    }
  }
}
