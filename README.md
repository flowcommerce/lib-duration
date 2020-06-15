[![Build Status](https://travis-ci.com/flowcommerce/lib-duration.svg?branch=master)](https://travis-ci.com/flowcommerce/lib-duration)

Scala library to make working with Flow's Common Duration object easier.

# Installation

```
   "io.flow" %% "lib-duration" % "0.0.1"
```

# Usage

```
    import io.flow.common.v0.models.Duration
    import io.flow.duration.InternalDuration


    val duration1 = InternalDuration(3, UnitOfTime.Month)
    val duration2 = InternalDuration(12, UnitOfTime.Hour)

    if (duration1.isShorterThan(duration2)) {
      println(s"Duration '${duration1.label}' is shorter than '${duration2.label}'")
    } else {
      println(s"Duration '${duration2.label}' is shorter than '${duration1.label}'")
    }

    val minDuration = Seq(duration1, duration2).min
    println(s"Shortest duration is: ${minDuration.label}")
```


Will produce:
```
Duration '12 hours' is shorter than '3 months'
Shortest duration is: 12 hours

```

# Key Features

  - Sorting: `InternalDuration` extends `Ordered` making durations shortable
  - `UnitOfTimeLabel` provides for a pretty label
