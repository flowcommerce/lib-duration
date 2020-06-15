package util

import io.flow.common.v0.models.{UnitOfTime, Duration}
import io.flow.common.v0.models.UnitOfTime._

object InternalDuration {
  def apply(duration: Duration): InternalDuration = {
    InternalDuration(duration.value, duration.unit)
  }
}

case class InternalDuration(value: Long, unitOfTime: UnitOfTime) extends Ordered[InternalDuration] {

  private[this] val MinutesPerHour = 60L
  private[this] val HoursPerDay = 24L
  private[this] val MinutesPerDay = MinutesPerHour * HoursPerDay

  val label: String = s"${value} ${UnitOfTimeLabel.pluralize(value, unitOfTime)}"

  private lazy val estimateInMinutes: Long = {
    value * (unitOfTime match {
      case Year => 365 * MinutesPerDay
      case Month => 30 * MinutesPerDay
      case Week => 7 * MinutesPerDay
      case Day => MinutesPerDay
      case Hour => MinutesPerHour
      case Minute => 1
      case UNDEFINED(_) => Long.MaxValue
    })
  }

  def isShorterThan(that: InternalDuration): Boolean = compare(that) < 0

  /**
   * Compares two internal durations. If the unit of time is
   * undefined, we consider the two durations equal
   */
  override def compare(that: InternalDuration): Int = {
    if (this.unitOfTime == that.unitOfTime) {
      this.value.compare(that.value)
    } else {
      // Find smallest units that we can both convert to. If not found
      // use estimated minutes
      Seq(
        (this.minutes, that.minutes),
        (this.hours, that.hours),
        (this.days, that.days),
        (this.weeks, that.weeks),
        (this.months, that.months),
        (this.years, that.years),
      ).view.flatMap {
        case (Some(a), Some(b)) => Some(a.compare(b))
        case (_, _) => None
      }.headOption.getOrElse {
        this.estimateInMinutes.compare(that.estimateInMinutes)
      }
    }
  }

  // Exact number of years or None if cannot be converted exactly
  protected[util] val years: Option[Long] = {
    unitOfTime match {
      case Year => Some(value)
      case Month | Week | Day | Hour | Minute => None
      case UNDEFINED(_) => None
    }
  }

  // Exact number of months or None if cannot be converted exactly
  protected[util] val months: Option[Long] = {
    unitOfTime match {
      case Year => Some(value * 12)
      case Month => Some(value)
      case Week | Day | Hour | Minute => None
      case UNDEFINED(_) => None
    }
  }

  // Exact number of weeks or None if cannot be converted exactly
  protected[util] val weeks: Option[Long] = {
    unitOfTime match {
      case Year | Month => None
      case Week => Some(value)
      case Day | Hour | Minute => ifWhole(MinutesPerDay * 7)
      case UNDEFINED(_) => None
    }
  }

  // Exact number of days or None if cannot be converted exactly
  protected[util] val days: Option[Long] = {
    unitOfTime match {
      case Year | Month => None
      case Week => Some(value * 7)
      case Day => Some(value)
      case Hour | Minute => ifWhole(MinutesPerDay)
      case UNDEFINED(_) => None
    }
  }

  // Exact number of hours or None if cannot be converted exactly
  protected[util] val hours: Option[Long] = {
    unitOfTime match {
      case Year | Month => None
      case Week => Some(value * 7 * 24)
      case Day => Some(value * 24)
      case Hour => Some(value)
      case Minute => ifWhole(MinutesPerHour)
      case UNDEFINED(_) => None
    }
  }

  // Exact number of minutes or None if cannot be converted exactly
  protected[util] val minutes: Option[Long] = {
    unitOfTime match {
      case Year | Month => None
      case Week => Some(value * 7 * MinutesPerDay)
      case Day => Some(value * MinutesPerDay)
      case Hour => Some(value * MinutesPerHour)
      case Minute => Some(value)
      case UNDEFINED(_) => None
    }
  }

  // If the estimate in minutes is a multiple of divisor, returns
  // that value. eg. 24 hours is 1 day
  private[this] def ifWhole(divisor: Long): Option[Long] = {
    if (estimateInMinutes % divisor == 0) {
      Some(estimateInMinutes / divisor)
    } else {
      None
    }
  }
}

object UnitOfTimeLabel {

  def pluralize(value: Number, unitOfTime: UnitOfTime): String = {
    import io.flow.common.v0.models.UnitOfTime._
    if (value == 1) {
      unitOfTime.toString
    } else {
      unitOfTime match {
        case Year => "years"
        case Month => "months"
        case Week => "weeks"
        case Day => "days"
        case Hour => "hours"
        case Minute => "minutes"
        case UNDEFINED(other) => s"${other}s"
      }
    }
  }
}
