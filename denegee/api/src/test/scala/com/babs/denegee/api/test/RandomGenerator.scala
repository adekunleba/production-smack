package com.babs.denegee.api.test

import java.time.{Instant, ZonedDateTime}
import java.util.Calendar.YEAR

import org.scalacheck._
import org.scalacheck.rng.Seed
import org.scalacheck.ScalacheckShapeless

/**ScalaCheck shapeless is available to create Arbitrary random generation of case classes**/
trait RandomGenerator extends ScalacheckShapeless {

  private def seed = Seed.random()
  implicit val arbitrary = Arbitrary(Gen.asciiPrintableStr)

  def randomGen[T: Arbitrary]: Gen[T] = Arbitrary.arbitrary[T]

  def random[T](implicit arbitrary: Arbitrary[T]): T =
    randomGen[T].apply(Gen.Parameters.default, seed).get

  def random[T](gen: Gen[T]): T =
    random(Arbitrary(gen))

  implicit val instantArbitrary: Arbitrary[Instant] = {
    val currentYear = ZonedDateTime.now().getYear
    val timespan = 10

    Arbitrary(
      for {
        year <- Gen.chooseNum(currentYear - timespan, currentYear + timespan)
        calendar <- Gen.calendar

        _ = calendar.set(YEAR, year)
      } yield calendar.toInstant
    )
  }

}
