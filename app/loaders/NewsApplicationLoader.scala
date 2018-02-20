package loaders

import java.time.Clock

import controllers._
import play.api.ApplicationLoader.Context
import play.api.{Application, ApplicationLoader, BuiltInComponents, BuiltInComponentsFromContext}
import play.filters.HttpFiltersComponents
import play.modules.reactivemongo._
import repos.NewsMongoRepo
import router.Routes
import services.ApplicationTimer

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

    lazy val homeController = new HomeController(controllerComponents)

    lazy val applicationTimer = new ApplicationTimer(Clock.systemDefaultZone(), applicationLifecycle)
    lazy val newsController = new NewsController(newsRepo, controllerComponents)

    lazy val router = new Routes(httpErrorHandler, homeController,
      newsController, assets)
  }
}

trait ReactiveMongoClient { self: BuiltInComponents =>
  lazy val mongoApi: ReactiveMongoApi = new DefaultReactiveMongoApi(configuration, applicationLifecycle)
}

trait MongoRepos { self: ReactiveMongoClient =>
  lazy val newsRepo: NewsMongoRepo = new NewsMongoRepo { val mongoApi = self.mongoApi }
}
