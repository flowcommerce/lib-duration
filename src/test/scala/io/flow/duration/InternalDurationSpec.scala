package util

import io.flow.common.v0.models.UnitOfTime
import io.flow.common.v0.models.UnitOfTime._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers

class InternalDurationSpec extends AnyWordSpec with Matchers {

  "label" in {
    def test(value: Long, unit: UnitOfTime) = InternalDuration(value, unit).label

    test(1, Year) must equal("1 year")
    test(2, Year) must equal("2 years")
    test(1, Month) must equal("1 month")
    test(2, Month) must equal("2 months")
    test(1, Week) must equal("1 week")
    test(2, Week) must equal("2 weeks")
    test(1, Day) must equal("1 day")
    test(2, Day) must equal("2 days")
    test(1, Hour) must equal("1 hour")
    test(2, Hour) must equal("2 hours")
    test(1, Minute) must equal("1 minute")
    test(2, Minute) must equal("2 minutes")
  }

  "isShorter" in {
    def test(valueA: Long, unitA: UnitOfTime, valueB: Long, unitB: UnitOfTime) = {
      InternalDuration(valueA, unitA).isShorterThan(
        InternalDuration(valueB, unitB)
      )
    }

    test(1, Year, 1, Year) must be(false)
    test(1, Year, 2, Year) must be(true)
    test(2, Year, 1, Year) must be(false)

    test(1, Year, 11, Month) must be(false)
    test(1, Year, 12, Month) must be(false)
    test(1, Year, 13, Month) must be(true)
    test(13, Month, 1, Year) must be(false)
    test(12, Month, 1, Year) must be(false)
    test(11, Month, 1, Year) must be(true)

    test(1, Month, 3, Week) must be(false)
    test(1, Month, 4, Week) must be(false)
    test(1, Month, 5, Week) must be(true)
  }

  "compare" in {
    def test(valueA: Long, unitA: UnitOfTime, valueB: Long, unitB: UnitOfTime) = {
      InternalDuration(valueA, unitA).compare(
        InternalDuration(valueB, unitB)
      )
    }

    test(1, Year, 1, Year) must be(0)
    test(1, Year, 2, Year) must be(-1)
    test(2, Year, 1, Year) must be(1)

    test(1, Year, 12, Month) must be(0)
    test(1, Year, 13, Month) must be(-1)
    test(1, Year, 11, Month) must be(1)
  }

  "years" in {
    def test(unit: UnitOfTime, value: Long = 1) = InternalDuration(value, unit).years

    test(Year) must equal(Some(1))
    test(Year,2) must equal(Some(2))
    test(Month) must equal(None)
    test(Week) must equal(None)
    test(Day) must equal(None)
    test(Hour) must equal(None)
    test(Minute) must equal(None)
  }

  "months" in {
    def test(unit: UnitOfTime, value: Long = 1) = InternalDuration(value, unit).months

    test(Year) must equal(Some(12))
    test(Year, 2) must equal(Some(24))
    test(Month) must equal(Some(1))
    test(Month, 2) must equal(Some(2))
    test(Week) must equal(None)
    test(Day) must equal(None)
    test(Hour) must equal(None)
    test(Minute) must equal(None)
  }

  "weeks" in {
    def test(unit: UnitOfTime, value: Long = 1) = InternalDuration(value, unit).weeks

    test(Year) must equal(None)
    test(Month) must equal(None)
    test(Week) must equal(Some(1))
    test(Week, 2) must equal(Some(2))
    test(Day) must equal(None)
    test(Day, 7) must equal(Some(1))
    test(Day, 14) must equal(Some(2))
    test(Hour) must equal(None)
    test(Hour, 24 * 7) must equal(Some(1))
    test(Hour, 48 * 7) must equal(Some(2))
    test(Minute) must equal(None)
    test(Minute, 24*60*7) must equal(Some(1))
    test(Minute, 48*60*7) must equal(Some(2))
  }

  "days" in {
    def test(unit: UnitOfTime, value: Long = 1) = InternalDuration(value, unit).days

    test(Year) must equal(None)
    test(Month) must equal(None)
    test(Week) must equal(Some(7))
    test(Week, 2) must equal(Some(14))
    test(Day) must equal(Some(1))
    test(Day, 2) must equal(Some(2))
    test(Hour) must equal(None)
    test(Hour, 24) must equal(Some(1))
    test(Hour, 48) must equal(Some(2))
    test(Minute) must equal(None)
    test(Minute, 24*60) must equal(Some(1))
    test(Minute, 48*60) must equal(Some(2))
  }

  "minutes" in {
    def test(unit: UnitOfTime, value: Long = 1) = InternalDuration(value, unit).minutes

    test(Year) must equal(None)
    test(Month) must equal(None)
    test(Week) must equal(Some(10080))
    test(Week, 2) must equal(Some(20160))
    test(Day) must equal(Some(1440))
    test(Day, 2) must equal(Some(2880))
    test(Hour) must equal(Some(60))
    test(Hour, 2) must equal(Some(120))
    test(Minute) must equal(Some(1))
    test(Minute, 2) must equal(Some(2))
  }

}
