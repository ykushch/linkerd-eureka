
def twitterUtil(mod: String) =
  "com.twitter" %% s"util-$mod" % "6.45.0"

def finagle(mod: String) =
  "com.twitter" %% s"finagle-$mod" % "6.45.0"

def linkerd(mod: String) =
  "io.buoyant" %% s"linkerd-$mod" % "1.2.0"

def eurekaClient =
  "com.netflix.eureka" % s"eureka-client" % "1.8.3" % "compile"

def eurekaCore =
  "com.netflix.eureka" % s"eureka-core" % "1.8.3" % "compile"

val eurekaModule =
  project.in(file("eureka-module")).
    settings(
      scalaVersion := "2.12.1",
      organization := "net.ykushch",
      name := "eureka-integration",
      resolvers ++= Seq.apply(
        "twitter" at "https://maven.twttr.com",
        "local-m2" at ("file:" + Path.userHome.absolutePath + "/.m2/repository")
      ),
      libraryDependencies ++=
        finagle("http") % "provided" ::
          twitterUtil("core") % "provided" ::
          linkerd("core") % "provided" ::
          linkerd("protocol-http") % "provided" ::
          eurekaCore ::
          eurekaClient ::
          Nil,
      assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
    )
