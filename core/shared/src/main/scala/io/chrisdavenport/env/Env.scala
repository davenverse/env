package io.chrisdavenport.env

import cats.{~>, Applicative, Functor}
import cats.data.{EitherT, IorT, Kleisli, OptionT, ReaderWriterStateT, StateT, WriterT}
import cats.effect.kernel.Sync
import cats.kernel.Monoid

import scala.collection.immutable.Map

/**
 * Effect type agnostic `Env` with common methods to read environment variables
 */
trait Env[F[_]] { self =>

  def get(name: String): F[Option[String]]

  def toMap: F[Map[String, String]]

  def mapK[G[_]](f: F ~> G): Env[G] = new Env[G] {
    def get(name: String): G[Option[String]] = f(self.get(name))
    def toMap: G[Map[String, String]] = f(self.toMap)
  }
}

object Env extends EnvCompanionPlatform {

  /**
   * Summoner method for `Env` instances.
   */
  def apply[F[_]](implicit E: Env[F]): E.type = E

  /**
   * Constructs a `Env` instance for `F` data types that are cats.effect.kernel.Sync.
   */
  def make[F[_]](implicit F: Sync[F]): Env[F] = new SyncEnv[F]

  /**
   * [[Env]] instance built for `cats.data.EitherT` values initialized with any `F` data type
   * that also implements `Env`.
   */
  implicit def catsEitherTEnv[F[_]: Env: Functor, L]: Env[EitherT[F, L, *]] =
    Env[F].mapK(EitherT.liftK)

  /**
   * [[Env]] instance built for `cats.data.Kleisli` values initialized with any `F` data type
   * that also implements `Env`.
   */
  implicit def catsKleisliEnv[F[_]: Env, R]: Env[Kleisli[F, R, *]] =
    Env[F].mapK(Kleisli.liftK)

  /**
   * [[Env]] instance built for `cats.data.OptionT` values initialized with any `F` data type
   * that also implements `Env`.
   */
  implicit def catsOptionTEnv[F[_]: Env: Functor]: Env[OptionT[F, *]] =
    Env[F].mapK(OptionT.liftK)

  /**
   * [[Env]] instance built for `cats.data.StateT` values initialized with any `F` data type
   * that also implements `Env`.
   */
  implicit def catsStateTEnv[F[_]: Env: Applicative, S]: Env[StateT[F, S, *]] =
    Env[F].mapK(StateT.liftK)

  /**
   * [[Env]] instance built for `cats.data.WriterT` values initialized with any `F` data type
   * that also implements `Env`.
   */
  implicit def catsWriterTEnv[
      F[_]: Env: Applicative,
      L: Monoid
  ]: Env[WriterT[F, L, *]] =
    Env[F].mapK(WriterT.liftK)

  /**
   * [[Env]] instance built for `cats.data.IorT` values initialized with any `F` data type that
   * also implements `Env`.
   */
  implicit def catsIorTEnv[F[_]: Env: Functor, L]: Env[IorT[F, L, *]] =
    Env[F].mapK(IorT.liftK)

  /**
   * [[Env]] instance built for `cats.data.ReaderWriterStateT` values initialized with any `F`
   * data type that also implements `Env`.
   */
  implicit def catsReaderWriterStateTEnv[
      F[_]: Env: Applicative,
      E,
      L: Monoid,
      S
  ]: Env[ReaderWriterStateT[F, E, L, S, *]] =
    Env[F].mapK(ReaderWriterStateT.liftK)
}