package io.chrisdavenport.env

import cats.data.OptionT
import cats.effect.kernel.Sync
import cats.syntax.all._

import scala.collection.immutable.Map
import scala.scalajs.js
import scala.util.Try

private[env] class EnvCompanionPlatform {
  private[env] final class SyncEnv[F[_]](implicit F: Sync[F]) extends Env[F] {
    def get(name: String): F[Option[String]] =
      OptionT(F.delay(processEnv.get(name))).collect {
        case value: String => value // JavaScript. 'nuff said
      }.value

    def toMap: F[Map[String, String]] =
      F.delay(processEnv.collect { case (name, value: String) => name -> value }.toMap)

    private def processEnv =
      Try(js.Dynamic.global.process.env.asInstanceOf[js.Dictionary[Any]])
        .getOrElse(js.Dictionary.empty)
  }
}