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
import reactivemongo.play.json.collection.JSONCollection

@Singleton
class Users @Inject()(val messagesApi: MessagesApi, val reactiveMongoApi: ReactiveMongoApi)  extends Controller with I18nSupport with MongoController with ReactiveMongoComponents {



  def collection: JSONCollection = db.collection[JSONCollection]("user")

  //Created a simple form.

  val formMapping = mapping (
    "name" -> nonEmptyText,
    "email" -> email
  )(User.apply)(User.unapply)

  val userForm: Form[User] = Form(formMapping)

  def index = TODO



  def add = Action {

        Ok(views.html.user.form(userForm))

  }

  def save = Action.async {
    implicit request =>

      userForm.bindFromRequest.fold(
        errors => {

          Future.successful(
            BadRequest(views.html.user.form(errors))
          )
        },

        user => {

          val futureResult = collection.insert(user)

          futureResult.map(r=> Ok(r.message))

          Future.successful(

            Redirect("/")

          )

        }
      )

  }
}