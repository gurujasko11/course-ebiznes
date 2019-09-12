package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.impl.providers._
import models.{ MyUser, MyUserRepository }
import models.services.UserService
import play.api.i18n.{ I18nSupport, Messages }
import play.api.libs.json.Json
import play.api.mvc.{ AbstractController, AnyContent, ControllerComponents, Request }
import play.filters.csrf.CSRF.Token
import utils.auth.DefaultEnv

import scala.concurrent.{ ExecutionContext, Future }

class SocialAuthController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  socialProviderRegistry: SocialProviderRegistry,
  userRepo: MyUserRepository
)(
  implicit
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport with Logger {

  var loggedUserEmail: String = ""

  def authenticate(provider: String) = Action.async { implicit request: Request[AnyContent] =>
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate().flatMap {
          case Left(result) => Future.successful(result)
          case Right(authInfo) => for {
            profile <- p.retrieveProfile(authInfo)
            user <- userService.save(profile)
            authInfo <- authInfoRepository.save(profile.loginInfo, authInfo)
            authenticator <- silhouette.env.authenticatorService.create(profile.loginInfo)
            value <- silhouette.env.authenticatorService.init(authenticator)
            result <- silhouette.env.authenticatorService.embed(value, Redirect("http://localhost:3000"))
          } yield {
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            userRepo.isEmailExist(user.email.getOrElse("No email")).map(isExist =>
              if (!isExist) {
                userRepo.create("no login", "no password", user.email.getOrElse("no email"), "no phone")
              }
            )
            loggedUserEmail = user.email.getOrElse("No email")
            result
          }
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
    }).recover {
      case e: ProviderException =>
        logger.error("Unexpected provider error", e)
        Redirect("http://localhost:3000/").flashing("error" -> Messages("could.not.authenticate"))
    }
  }

  def getLoggedInEmail = Action.async { implicit request =>
    println("getLoggedInEmail loggedUserEmail " + loggedUserEmail)
    userRepo.getByEmail(loggedUserEmail).map { user: Seq[MyUser] =>
      Ok(Json.toJson(user)).withHeaders(
        "Access-Control-Allow-Origin" -> "*")
    }
  }

  def signOut = Action { implicit request =>
    loggedUserEmail = ""
    Ok(views.html.home())
  }
}
