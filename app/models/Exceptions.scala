package models

object Exceptions {

  // Entity is not found
  case class NotFoundException[T](params: T) extends Exception(s"Row not found for params: $params")

  // Found more that 1 result, when expected exactly one
  case class TooManyFoundException[T](params: T) extends Exception(s"Found too many rows for params: $params")
}
