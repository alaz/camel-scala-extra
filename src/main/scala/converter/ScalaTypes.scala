package com.osinka.camel.scala
package converter

import org.apache.camel.Converter

@Converter
object ScalaTypes {
  /**
   * org.apache.camel:camel-scala provides conversions for XML
   */

  @Converter
  def toString(symbol: Symbol): String = symbol.toString

  @Converter
  def toSymbol(string: String): Symbol = Symbol(string)
}
