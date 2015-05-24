package org.vocabstar

import java.net.URL

object WordCategory extends Enumeration {
  type WordCategory = Value
  val Noun, Pronoun, Verb, Adjective, Adverb, Participle, Article, Preposition =
    Value
}

import WordCategory._

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
