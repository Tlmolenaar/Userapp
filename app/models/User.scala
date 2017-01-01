package models

import play.api.libs.json.Json

case class User (name: String, email: String)

object User {

  implicit val userJsonFormat = Json.format[User]

}