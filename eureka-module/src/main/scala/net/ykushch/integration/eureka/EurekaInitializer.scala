package net.ykushch.integration.eureka

import com.fasterxml.jackson.annotation.JsonIgnore
import com.twitter.finagle.{Stack, Path}
import io.buoyant.config.types.{HostAndPort, Port}
import io.buoyant.namer.{NamerConfig, NamerInitializer}

class EurekaInitializer extends NamerInitializer {
  val configClass: Class[EurekaConfig] = classOf[EurekaConfig]
  override def configId = "io.l5d.eureka"
}

object ServersetsInitializer extends EurekaInitializer

case class EurekaConfig(ekAddrs: Seq[HostAndPort]) extends NamerConfig {

  @JsonIgnore
  override def defaultPrefix: Path = Path.read("/io.l5d.eureka")

  @JsonIgnore
  val connectString: String = ekAddrs.map(_.toString(defaultPort = Port.apply(8761))).mkString(",")

  /**
    * Construct a namer.
    */
  def newNamer(params: Stack.Params) = new EurekaNamer(connectString, prefix)
}