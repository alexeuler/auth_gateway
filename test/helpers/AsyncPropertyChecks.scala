package helpers

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.prop.PropertyChecks

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait AsyncPropertyChecks {
  propertyChecks: PropertyChecks =>

  private val timeout = 5000 millis

  def forAllAsync[A, R](f: A => Future[R])(implicit arbA: Arbitrary[A]): Unit = {
    forAll { a: A =>
      val future = f(a)
      Await.result(future, timeout)
    }
  }

  def forAllAsync[A, B, R](f: (A, B) => Future[R])(implicit arbA: Arbitrary[A], arbB: Arbitrary[B]): Unit = {
    forAll { (a: A, b: B) =>
      val future = f(a, b)
      Await.result(future, timeout)
    }
  }

  def forAllAsync[A, B, C, R](f: (A, B, C) => Future[R])
                                     (implicit arbA: Arbitrary[A],
                                      arbB: Arbitrary[B],
                                      arbC: Arbitrary[C]): Unit = {
    forAll { (a: A, b: B, c: C) =>
      val future = f(a, b, c)
      Await.result(future, timeout)
    }
  }

  def forAllAsync[A, B, C, D, R](f: (A, B, C, D) => Future[R])
                                        (implicit arbA: Arbitrary[A],
                                         arbB: Arbitrary[B],
                                         arbC: Arbitrary[C],
                                         arbD: Arbitrary[D]): Unit = {
    forAll { (a: A, b: B, c: C, d: D) =>
      val future = f(a, b, c, d)
      Await.result(future, timeout)
    }
  }

  def forAllAsync[A, R](genA: Gen[A])(f: A => Future[R]): Unit = {
    forAll(genA) { a: A =>
      val future = f(a)
      Await.result(future, timeout)
    }
  }

  def forAllAsync[A, B, R](genA: Gen[A], genB: Gen[B])(f: (A, B) => Future[R]): Unit = {
    forAll(genA, genB) { (a: A, b: B) =>
      val future = f(a, b)
      Await.result(future, timeout)
    }
  }

  def forAllAsync[A, B, C, R](genA: Gen[A], genB: Gen[B], genC: Gen[C])
                                        (f: (A, B, C) => Future[R]): Unit = {
    forAll(genA, genB, genC) { (a: A, b: B, c: C) =>
      val future = f(a, b, c)
      Await.result(future, timeout)
    }
  }

  def forAllAsync[A, B, C, D, R](genA: Gen[A], genB: Gen[B], genC: Gen[C], genD: Gen[D])
                                        (f: (A, B, C, D) => Future[R]): Unit = {
    forAll(genA, genB, genC, genD) { (a: A, b: B, c: C, d: D) =>
      val future = f(a, b, c, d)
      Await.result(future, timeout)
    }
  }

}
