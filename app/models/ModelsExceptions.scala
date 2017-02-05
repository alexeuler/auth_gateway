package models

object ModelsExceptions {

  // Found more that 1 result, when expected exactly one
  case class TooManyFoundException[T](params: T) extends Exception(s"Found too many rows for params: $params")

  case class AlreadyExists[T](params: T) extends Exception(s"Entity already exists: $params")
}
