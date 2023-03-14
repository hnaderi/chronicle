import sbtcrossproject.CrossProject

lazy val scala3 = "3.2.2"
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

def module(mname: String): CrossProject => CrossProject =
  _.in(file(s"modules/$mname"))
    .settings(
      name := s"module-$mname",
      libraryDependencies ++= Seq(
        "org.scalameta" %%% "munit" % "1.0.0-M7" % Test,
        "org.scalameta" %%% "munit-scalacheck" % "1.0.0-M7" % Test
      ),
      moduleName := s"chronicle-$mname"
    )

lazy val root = tlCrossRootProject
  .aggregate(core, docs)
  .settings(
    name := "chronicle"
  )

lazy val core = module("core") {
  crossProject(JVMPlatform, JSPlatform, NativePlatform)
    .crossType(CrossType.Pure)
    .settings(
      libraryDependencies ++= Seq(
        "dev.hnaderi" %%% "edomata-backend" % "0.9.1",
        "dev.hnaderi" %%% "named-codec" % "0.1.0",
        "org.typelevel" %%% "munit-cats-effect" % "2.0.0-M3" % Test
      )
    )
}

lazy val skunk = module("skunk") {
  crossProject(JVMPlatform, JSPlatform, NativePlatform)
    .crossType(CrossType.Pure)
    .settings(
      description := "Skunk based backend for edomata",
      libraryDependencies ++= Seq(
        "org.tpolecat" %%% "skunk-core" % Versions.skunk,
        "org.typelevel" %%% "munit-cats-effect-3" % Versions.CatsEffectMunit % Test
      )
    )
    .jsSettings(
      Test / scalaJSLinkerConfig ~= (_.withModuleKind(
        ModuleKind.CommonJSModule
      ))
    )
}

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
