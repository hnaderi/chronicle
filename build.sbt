ThisBuild / tlBaseVersion := "0.0"

ThisBuild / organization := "dev.hnaderi"
ThisBuild / organizationName := "Hossein Naderi"
ThisBuild / startYear := Some(2022)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  tlGitHubDev("hnaderi", "Hossein Naderi")
)
ThisBuild / tlSonatypeUseLegacyHost := false
ThisBuild / tlSitePublishBranch := Some("main")
ThisBuild / scalaVersion := "3.1.3"

lazy val root = tlCrossRootProject
  .aggregate(core, docs)
  .settings(
    name := "chronicle"
  )

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "chronicle",
    libraryDependencies ++= Seq(
      "dev.hnaderi" %%% "edomata-sql-backend" % "0.6.0",
      "org.scalameta" %%% "munit" % "0.7.29" % Test,
      "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7" % Test
    )
  )


import laika.rewrite.link.ApiLinks
import laika.rewrite.link.LinkConfig

lazy val docs = project
  .in(file("site"))
  .enablePlugins(TypelevelSitePlugin)
  .settings(
    tlSiteHeliumConfig := SiteConfigs(mdocVariables.value),
    tlSiteRelatedProjects := Seq(
      "Edomata" -> url("https://edomata.ir"),
      TypelevelProject.Cats,
      TypelevelProject.CatsEffect,
      TypelevelProject.Fs2
    ),
    // laikaIncludeAPI := true
  )
