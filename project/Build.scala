import sbt._

import Keys._
import AndroidKeys._

object General {
  val settings = Defaults.defaultSettings ++ Seq (
    name := "api",
    organization := "rtkaczyk.eris",
    crossPaths := false,
    version := "0.1",
    versionCode := 0,
    scalaVersion := "2.9.2",
    platformName in Android := "android-8"
  )

  val proguardSettings = Seq (
    useProguard in Android := true
  )

  lazy val fullAndroidSettings =
    General.settings ++
    AndroidProject.androidSettings ++
    //TypedResources.settings ++
    proguardSettings ++
    AndroidManifestGenerator.settings /*++
    AndroidMarketPublish.settings ++ Seq (
      keyalias in Android := "change-me"
    ) ++
    addArtifact(Artifact("api", "apklib", "apklib"), apklibPackage in Android).settings*/
}

object AndroidBuild extends Build {
  lazy val main = Project (
    "ErisApi",
    file("."),
    settings = General.fullAndroidSettings
  )
}
