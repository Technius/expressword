package org.vocabstar

import java.net.URL

sealed trait WordCategory
case object Noun        extends WordCategory
case object Pronoun     extends WordCategory
case object Verb        extends WordCategory
case object Adjective   extends WordCategory
case object Adverb      extends WordCategory
case object Participle  extends WordCategory
case object Article     extends WordCategory
case object Preposition extends WordCategory

case class Definition(category: WordCategory, text: String)

case class Vocabulary(
    word: String,
    definitions: Seq[Definition],
    sentences: Seq[String],
    aggregate: Seq[Item])

sealed trait Item {
  def url: URL
}

case class WebpageItem(url: URL) extends Item
case class NewsItem(url: URL, summary: String) extends Item
case class VideoItem(url: URL, description: String) extends Item
