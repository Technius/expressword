package org.expressword

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

case class Item(url: String, title: String, snippet: String,
    thumbnail: Option[String] = None)
