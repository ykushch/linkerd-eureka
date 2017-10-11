package net.ykushch.integration.eureka

import com.twitter.finagle._
import com.twitter.util.{Activity, Var}

class EurekaNamer(ekHost: String, idPrefix: Path) extends Namer {

  /** Resolve a resolver string to a Var[Addr]. */
  protected[this] def resolve(spec: String): Var[Addr] = Resolver.eval(spec) match {
    case Name.Bound(addr) => addr
    case _ => Var.value(Addr.Neg)
  }

  protected[this] def resolveServerset(hosts: String, path: String): Var[Addr] =
  // TODO: should be more generic. Old method for resolve resolve(s"inet!$hosts!$path")
    resolve(s"inet!localhost:8761")

  protected[this] def resolveServerset(hosts: String, path: String, endpoint: String): Var[Addr] =
  //TODO: should be more generic. Oleresolve(s"inet!$hosts!$path!$endpoint")
    resolve(s"inet!localhost:8761")

  /** Bind a name. */
  protected[this] def bind(path: Path, residual: Path = Path.empty): Activity[NameTree[Name]] = {
    // Clients may depend on Name.Bound ids being Paths which resolve
    // back to the same Name.Bound
    val id = idPrefix ++ path
    path match {
      case Path.Utf8(segments@_*) =>
        val addr = if (segments.nonEmpty && (segments.last contains ":")) {
          val Array(name, endpoint) = segments.last.split(":", 2)
          val ekPath = (segments.init :+ name).mkString("/", "/", "")
          resolveServerset(ekHost, ekPath, endpoint)
        } else {
          val ekPath = segments.mkString("/", "/", "")
          resolveServerset(ekHost, ekPath)
        }

        val act = Activity.apply(addr.map(Activity.Ok.apply))

        act.flatMap {
          case Addr.Neg if !path.isEmpty =>
            val n = path.size
            bind(path.take(n - 1), path.drop(n - 1) ++ residual)
          case Addr.Neg =>
            Activity.value(NameTree.Neg)
          case Addr.Bound(_, _) =>
            Activity.value(NameTree.Leaf.apply(Name.Bound.apply(addr, id, residual)))
          case Addr.Pending =>
            Activity.pending
          case Addr.Failed(exc) =>
            Activity.exception(exc)
        }
    }
  }

  // We have to involve a serverset roundtrip here to return a tree. We run the
  // risk of invalidating an otherwise valid tree when there is a bad serverset
  // on an Alt branch that would never be taken. A potential solution to this
  // conundrum is to introduce some form of lazy evaluation of name trees.
  def lookup(path: Path): Activity[NameTree[Name]] = bind(path)
}