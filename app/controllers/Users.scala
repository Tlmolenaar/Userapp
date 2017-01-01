package controllers

import models.User
import play.api._
import javax.inject.{Inject, Singleton}

import play.api.data._
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import reactivemongo.api.Cursor
import reactivemongo.play.json._

@Singleton
class Users @Inject()(val messagesApi: MessagesApi, val reactiveMongoApi: ReactiveMongoApi)  extends Controller with I18nSupport with MongoController with ReactiveMongoComponents {


  def collection: JSONCollection = db.collection[JSONCollection]("user")

  val formMapping = mapping (
    "name" -> nonEmptyText,
    "email" -> email
  )(User.apply)(User.unapply)

  val userForm: Form[User] = Form(formMapping)

  def index = TODO

  def add = Action.async {
    implicit request =>

      Future.successful(
        Ok(views.html.user.form(userForm)))
  }

  def save = TODO
}