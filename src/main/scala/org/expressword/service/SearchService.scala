package org.expressword.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorFlowMaterializer
import java.net.URLEncoder
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.expressword._
import org.expressword.util.Json4sMarshalling
import scala.concurrent.Future

import SearchService._

class SearchService(googleApiKey: String, cseId: String)
    (implicit val system: ActorSystem, mat: ActorFlowMaterializer)
    extends Json4sMarshalling {

  import system.dispatcher

  val searchUrl =
    "https://www.googleapis.com/customsearch/v1?num=5&fields=" +
    URLEncoder.encode("items(link,snippet,title,pagemap)", "UTF-8") + "&key=" +
    URLEncoder.encode(googleApiKey, "UTF-8") + "&cx=" + URLEncoder.encode(
    cseId, "UTF-8") + "&excludeTerms=dictionary"

  def scrapeData(vocab: Vocabulary): Future[Vocabulary] = {
    val searchTerm = URLEncoder.encode(vocab.word, "UTF-8")
    val request = HttpRequest(uri =
      searchUrl + s"&q=$searchTerm&exactTerm=$searchTerm")
    Http().singleRequest(request) flatMap { response =>
      Unmarshal(response).to[SearchResult] map { results =>
        val items = results.items.map { item =>
          val thumbnailOpt = item.pagemap.`cse_thumbnail`.headOption.map(_.src)
          Item(item.link, item.title, item.snippet, thumbnailOpt)
        }
        vocab.copy(aggregate = items)
      }
    }
  }
}

object SearchService {
  case class SearchResult(items: Seq[SearchItem])
  case class SearchItem(title: String, link: String, snippet: String,
      pagemap: SearchPagemap)
  case class SearchPagemap(`cse_thumbnail`: Seq[SearchThumbnail])
  case class SearchThumbnail(src: String)
}
