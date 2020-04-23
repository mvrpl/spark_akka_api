import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.apache.spark.sql.SparkSession
import com.typesafe.config.ConfigFactory

object WebServer extends App {

	implicit val system = ActorSystem()
	implicit val materializer = ActorMaterializer()
	implicit val executionContext = system.dispatcher

	val spark: SparkSession = SparkSession.builder().getOrCreate()

	val sc = spark.sparkContext

	val locale = new java.util.Locale("pt", "BR")
	val formatter = java.text.NumberFormat.getIntegerInstance(locale)

	val config = ConfigFactory.load()
	val host = config.getString("http.host")
	val port = config.getInt("http.port")

	val route =
		path("wc") {
			post {
				formFields('busca.as[String]) { (busca) =>
					val tempoInicial = System.nanoTime
					val resultado = formatter.format(sc.textFile("/home/marcos/*.txt").map(line => line.split(" ").count(_ == busca)).reduce(_ + _))
					val duracao = (System.nanoTime - tempoInicial) / 1e9d
					complete(s"O termo '${busca}' apareceu ${resultado}x\nA pesquisa demorou ${duracao} segundos\n")
				}
			}
		}

	val bindingFuture = Http().bindAndHandle(route, host, port)

	println(s"Servidor online em ${host}:${port}\nPressione ctrl+c para encerrar...")

	sys.addShutdownHook({
		println("\nDesligando WebServer...")
		spark.stop()
		system.terminate()
	})
}
