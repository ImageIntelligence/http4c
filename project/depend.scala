import sbt._

object Depend {
  lazy val http4sVersion = "0.18.0-M1"
  lazy val argonautVersion = "6.2"

  lazy val argonaut = Seq("io.argonaut" %% "argonaut" % argonautVersion)

  lazy val http4s = Seq(
    "org.http4s"  %% "http4s-dsl",
    "org.http4s"  %% "http4s-argonaut"
  ).map(_ % http4sVersion).map(_.withSources)

  lazy val http4sServer = Seq(
    "org.http4s"  %% "http4s-blaze-server"
  ).map(_ % http4sVersion).map(_ % "test")

  lazy val scalaTestCheck = Seq(
    "org.scalatest"   %% "scalatest"  % "3.0.4",
    "org.scalacheck"  %% "scalacheck" % "1.13.4"
  ).map(_.withSources).map(x => x.force()).map(_ % "test")

  lazy val depResolvers = Seq(
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
    "JCenter Bintray Repo" at "http://jcenter.bintray.com",
    Resolver.sonatypeRepo("releases")
  )

  lazy val dependencies =
    argonaut ++
    http4s ++
    http4sServer ++
    scalaTestCheck
}
