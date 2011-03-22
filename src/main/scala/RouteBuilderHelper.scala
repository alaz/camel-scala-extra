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

import annotation.implicitNotFound
import reflect.Manifest
import org.apache.camel.{Exchange, Message, Processor, Predicate, RuntimeTransformException}
import org.apache.camel.processor.aggregate.AggregationStrategy
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

  implicit def enrichFnUnit(f: Exchange => Unit) = new ScalaProcessor(f)
  implicit def enrichUnit(f: => Unit) = new ScalaProcessor( (_: Exchange) => f )
  implicit def enrichFnAny(f: Exchange => Any) = new ScalaPredicate(f)
  implicit def enrichAggr(f: (Exchange, Exchange) => Exchange) = new FnAggregationStrategy(f)

  /**
   * process { in(classOf[String]) { _+"11" } .toIn }
   * process { in[Int] { 11+ } .toOut }
   * 
   * process(in[Event] {
   *   case event: LoginEvent => doSession(event)
   *   case event: LogoutEvent => removeSession(event)
   * })
   */
  def in[T](clazz: Class[T]) = new BodyExtractor[T](_.getIn.getBody(clazz).asInstanceOf[T])

  /**
   * process { out { (s: String) => s+"11" } .toIn }
   * process { out[Int] { _+11 } .toOut }
   */
  def out[T](clazz: Class[T]) = new BodyExtractor[T](_.getOut.getBody(clazz).asInstanceOf[T])

  /**
   * filter { in[Int] { _ % 2 == 0 } }
   * filter { out { (s: String) => s.startsWith("aa") } }
   */
  implicit def wrapperFilter(w: WrappedProcessor) = w.predicate

  trait WrappedProcessor extends Processor {
    def run(exchange: Exchange): Option[Any]

    def toIn: Processor =
      (exchange: Exchange) =>
        run(exchange) foreach {
          case () => throw new RuntimeTransformException("Cannot save Unit result into message")
          case v => exchange.in = v
        }
      
    def toOut: Processor =
      (exchange: Exchange) =>
        run(exchange) foreach {
          case () => throw new RuntimeTransformException("Cannot save Unit result into message")
          case v => exchange.out = v
        }

    def predicate: Predicate =
      (exchange: Exchange) =>
        run(exchange) map {
          case () => throw new RuntimeTransformException("Unit result cannot be used in Predicate")
          case v => v
        } getOrElse false

    override def process(exchange: Exchange) {
      run(exchange) foreach {
        case () =>
        case v => exchange.in = v
      }
    }
  }

  class BodyExtractor[T](val get: (Exchange) => T) {
    def by(f: (T) => Any): WrappedProcessor = new FnProcessor(f)
    def collect(pf: PartialFunction[T,Any]): WrappedProcessor = new PfProcessor(pf)

    def apply(f: (T) => Any): WrappedProcessor = by(f)

    /**
     * Wrapper for function processor / predicate
     */
    class FnProcessor(val f: (T) => Any) extends WrappedProcessor {
      override def run(exchange: Exchange): Option[Any] = Some(f(get(exchange)))
    }

    /**
     * Wrapper for PartialFunction processor / predicate
     */
    class PfProcessor(val pf: PartialFunction[T,Any]) extends WrappedProcessor {
      override def run(exchange: Exchange): Option[Any] = PartialFunction.condOpt(get(exchange))(pf)
    }
  }

  /**
   * Wrapper for (Exchange, Exchange) => Exchange that acts as AggregationStrategy
   */
  class FnAggregationStrategy(aggregator: (Exchange, Exchange) => Exchange) extends AggregationStrategy {
    override def aggregate(original: Exchange, resource: Exchange): Exchange = aggregator(original, resource)
  }
}
