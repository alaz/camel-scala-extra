package com.osinka.camel.scala
package converter

import org.apache.camel.Converter

@Converter
class ScalaTypes {
  /**
   * org.apache.camel:camel-scala provides conversions for XML
   */

  @Converter
  def toString(symbol: Symbol): String = symbol.name

  @Converter
  def toSymbol(string: String): Symbol = Symbol(string)
}
