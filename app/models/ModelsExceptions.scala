package models

object ModelsExceptions {

  case class TestEx() extends Exception("123")

  // Found more that 1 result, when expected exactly one
  case class TooManyFoundException[T](params: T) extends Exception(s"Found too many rows for params: $params")
}
