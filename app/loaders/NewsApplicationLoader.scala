package loaders

import java.time.Clock

import controllers._
import play.api.ApplicationLoader.Context
import play.api.i18n.I18nComponents
import play.api.mvc.{DefaultMessagesControllerComponents, MessagesControllerComponents}
import play.api.{Application, ApplicationLoader, BuiltInComponents, BuiltInComponentsFromContext}
import play.filters.HttpFiltersComponents
import reactivemongo.api.{MongoConnection, MongoDriver}
import play.modules.reactivemongo._
import repos.MongoCategoryRepo
import router.Routes
import services.{ApplicationTimer, AtomicCounter}

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
class NewsApplicationLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): Application = {
    new OffersComponents(context).application
  }

  class OffersComponents(context: Context) extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with controllers.AssetsComponents
    with ReactiveMongoClient
    with MongoRepos {

    lazy val apiController = new ApiController(controllerComponents)
    lazy val homeController = new HomeController(controllerComponents)
    lazy val asyncController = new AsyncController(controllerComponents, actorSystem)

    lazy val applicationTimer = new ApplicationTimer(Clock.systemDefaultZone(), applicationLifecycle)
    lazy val counter = new AtomicCounter()
    lazy val countController = new CountController(controllerComponents, counter)
    lazy val categoryController = new CategoryController(controllerComponents, categoryRepo)
    lazy val newsController = new NewsController(controllerComponents)

    lazy val router = new Routes(httpErrorHandler, homeController, countController, asyncController, apiController,
      categoryController, newsController, assets)
  }
}

trait ReactiveMongoClient { self: BuiltInComponents =>
  lazy val mongoApi: ReactiveMongoApi = new DefaultReactiveMongoApi(configuration, applicationLifecycle)
}

trait MongoRepos { self: ReactiveMongoClient =>
  lazy val categoryRepo: MongoCategoryRepo = new MongoCategoryRepo { val mongoApi = self.mongoApi}
}
