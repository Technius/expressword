package org.vocabstar

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
  def url: String
}

case class WebpageItem(url: String) extends Item
case class NewsItem(url: String, summary: String) extends Item
case class VideoItem(url: String, description: String) extends Item
