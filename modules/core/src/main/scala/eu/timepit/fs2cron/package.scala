package eu.timepit

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

import cats.effect.Sync
import cron4s.expr.CronExpr
import cron4s.lib.javatime._
import fs2.Stream

import scala.concurrent.duration.FiniteDuration

package object fs2cron {

  def durationFrom(from: LocalDateTime, cronExpr: CronExpr): Option[FiniteDuration] =
    cronExpr.next(from).map { next =>
      val durationInMillis = from.until(next, ChronoUnit.MILLIS)
      FiniteDuration(durationInMillis, TimeUnit.MILLISECONDS)
    }

  def durationFromNow[F[_]: Sync](cronExpr: CronExpr): Stream[F, FiniteDuration] =
    evalNow.flatMap { now =>
      Stream.emits(durationFrom(now, cronExpr).toList)
    }

  def evalNow[F[_]](implicit F: Sync[F]): Stream[F, LocalDateTime] =
    Stream.eval(F.delay(LocalDateTime.now))

}
