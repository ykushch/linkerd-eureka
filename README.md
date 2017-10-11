# Eureka integration

TBD

## Building

This plugin is built with sbt. Run sbt from the plugins directory.

```
./sbt eurekaModule/assembly
```

This will produce the plugin jar at
`eureka-module/target/scala-2.12/eureka-module-assembly-0.1-SNAPSHOT.jar`.

## Installing

To install this plugin with linkerd, simply move the plugin jar into linkerd's
plugin directory (`$L5D_HOME/plugins`).  Then add a classifier block to the
router in your linkerd config.
