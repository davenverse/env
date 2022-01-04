package io.chrisdavenport.env

import cats.effect.kernel.Sync

import scala.collection.immutable.Map

private[env] class EnvCompanionPlatform {
  private[env] final class SyncEnv[F[_]](implicit F: Sync[F]) extends Env[F] {

    def get(name: String): F[Option[String]] =
      F.delay(Option(System.getenv(name))) // sys.env copies the entire env into a Map

    def toMap: F[Map[String, String]] =
      F.delay(Map(sys.env.toSeq:_*)) // a somewhat redundant copy, to shake the unsafe withDefault
  }
}