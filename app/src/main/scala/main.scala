import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes, ContentTypes}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer
import org.apache.spark.sql.SparkSession
import com.typesafe.config.ConfigFactory
import spray.json._

final case class PostData(path: String, filter: String, cols: List[String])

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val postDataFormat: RootJsonFormat[PostData] = jsonFormat3(PostData.apply)
}

object WebServer extends App with Directives with JsonSupport {
	implicit val system = ActorSystem()
	implicit val materializer = ActorMaterializer()
	implicit val executionContext = system.dispatcher

	val spark = SparkSession.builder().getOrCreate()

	val sc = spark.sparkContext

	val config = ConfigFactory.load()
	val host = config.getString("http.host")
	val port = config.getInt("http.port")

	val route =
		path("getData") {
			post {
				entity(as[PostData]) { (postData) =>
					val rowsDF = spark.read.parquet(postData.path).filter(postData.filter).selectExpr(postData.cols:_*).toJSON.take(500)
					val completeJSON = "[" ++ rowsDF.mkString(",") ++ "]"
					complete {
						HttpResponse(status = StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, completeJSON))
					}
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
