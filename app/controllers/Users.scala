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

import org.slf4j.MDC
import play.api.Logger

@Singleton
class Users @Inject()(val messagesApi: MessagesApi, val reactiveMongoApi: ReactiveMongoApi)  extends Controller with I18nSupport with MongoController with ReactiveMongoComponents {



  def collection: JSONCollection = db.collection[JSONCollection]("user")

  //The logger has been extended so it will provide both a timestamp and the thread
  // MDC adds information about the method to the logger

  val logger: Logger = Logger(this.getClass())

  //Created a simple form.

  val formMapping = mapping (
    "name" -> nonEmptyText,
    "email" -> email
  )(User.apply)(User.unapply)

  val userForm: Form[User] = Form(formMapping)

  //The index page automatically lists all the contacts in the database

  def index = Action.async { implicit request =>

    MDC.put("method", "all")
    logger.info("requested to retrieve all people")

    val cursor: Cursor[User] = collection.find(Json.obj()).cursor[User]

    val futureUserList: Future[List[User]] = cursor.collect[List]()

    futureUserList.map { user =>


      (Ok(views.html.index(user)))

    }
  }

  //Add leads to the view with the simple form

  def add = Action {

    logger.info("requested to add user")

    Ok(views.html.user.form(userForm))

  }

  /* When a new request is sent to the database the bindFromRequest method offers two options.
    If the request is invalid (if the entry can't be validated) the form is shown again together
    with standard error messages.

    If the request is processed correctly the controller will redirect to the index page where
    the new entry is shown in a list.
  */
  def save = Action.async {
    implicit request =>

      MDC.put("method", "save")
      logger.info("requested to save user")

      userForm.bindFromRequest.fold(
        errors => {

          logger.error(s"${errors.toString()} while trying to add user")

          Future.successful(
            BadRequest(views.html.user.form(errors)))
        },

        user => {

          logger.debug(s"user to add: ${user.toString}")
          logger.info("successfully added user")

          val futureResult = collection.insert(user)

          futureResult.map(r=> Ok(r.message))

          Future.successful(

            Redirect("/")

          )

        }
      )

  }
}