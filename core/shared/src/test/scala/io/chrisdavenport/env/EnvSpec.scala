package io.chrisdavenport.env

import munit.CatsEffectSuite
import cats.effect._

class EnvSpec extends CatsEffectSuite {

  implicit val env: Env[IO] = Env.make[IO]
  
  test("retrieve a variable from the environment") {
    Env[IO].get("HOME").map(x => assertEquals(x.isDefined, true, "HOME was not defined"))
  }
  test("return none for non-existent environment variable") {
    Env[IO].get("MADE_THIS_UP").map(x => assertEquals(x.isEmpty, true, "MADE_THIS_UP was set"))
  }
  test("create a map of all the things"){
    Env[IO].toMap.map(x => assertEquals(!x.isEmpty, true, "The Entire Env is empty"))
  }

}