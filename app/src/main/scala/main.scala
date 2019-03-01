import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.apache.spark.sql.SparkSession

object WebServer extends App {

	implicit val system = ActorSystem()
	implicit val materializer = ActorMaterializer()
	implicit val executionContext = system.dispatcher

	val spark: SparkSession = SparkSession.builder().getOrCreate()

	val sc = spark.sparkContext

	val locale = new java.util.Locale("pt", "BR")
	val formatter = java.text.NumberFormat.getIntegerInstance(locale)

	val route =
		path("wc") {
			post {
				formFields('busca.as[String]) { (busca) =>
					val tempoInicial = System.nanoTime
					val resultado = formatter.format(sc.textFile("/home/mvrpl/app/input/*.txt").map(line => line.split(" ").count(_ == busca)).reduce(_ + _))
					val duracao = (System.nanoTime - tempoInicial) / 1e9d
					complete(s"O termo '${busca}' apareceu ${resultado}x\nA pesquisa demorou ${duracao} segundos\n")
				}
			}
		}

	val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 5001)

	println(s"Servidor online em 0.0.0.0:5001\nPressione ctrl+c para encerrar...")

	sys.addShutdownHook({
		println("\nDesligando WebServer...")
		spark.stop()
		system.terminate()
	})
}