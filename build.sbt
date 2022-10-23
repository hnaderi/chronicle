lazy val scala3 = "3.1.3"
val PrimaryJava = JavaSpec.temurin("8")
val LTSJava = JavaSpec.temurin("17")

inThisBuild(
  List(
    tlBaseVersion := "0.0",
    scalaVersion := scala3,
    fork := true,
    Test / fork := false,
    organization := "dev.hnaderi",
    organizationName := "Hossein Naderi",
    startYear := Some(2022),
    tlSonatypeUseLegacyHost := false,
    tlCiReleaseBranches := Nil, // Seq("main"),
    tlSitePublishBranch := Some("main"),
    githubWorkflowJavaVersions := Seq(PrimaryJava, LTSJava),
    githubWorkflowBuildPreamble ++= dockerComposeUp,
    licenses := Seq(License.Apache2),
    developers := List(
      Developer(
        id = "hnaderi",
        name = "Hossein Naderi",
        email = "mail@hnaderi.dev",
        url = url("https://hnaderi.dev")
      )
    ),
    resolvers ++= Resolver.sonatypeOssRepos("snapshots")
  )
)

lazy val dockerComposeUp = Seq(
  WorkflowStep.Run(
    commands = List("docker-compose up -d"),
    name = Some("Start up Postgres")
  )
)

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
      "dev.hnaderi" %%% "edomata-backend" % "0.7-c1c6774-SNAPSHOT",
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
    )
    // laikaIncludeAPI := true
  )
