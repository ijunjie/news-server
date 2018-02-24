package loaders

import controllers._
import play.api.ApplicationLoader.Context
import play.api.{Application, ApplicationLoader, BuiltInComponents, BuiltInComponentsFromContext}
import play.filters.HttpFiltersComponents
import play.modules.reactivemongo._
import repos.{NewsMongoRepo, NewsRepo}
import router.Routes
import services.NewsService

import scala.concurrent.ExecutionContext

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
class NewsApplicationLoader extends ApplicationLoader {

  override def load(context: ApplicationLoader.Context): Application = {
    new NewsComponents(context).application
  }

  class NewsComponents(context: Context) extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with controllers.AssetsComponents
    with ReactiveMongoClient
    with MongoRepos
    with BusinessServices {

    override implicit lazy val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

    lazy val homeController = new HomeController(controllerComponents)
    lazy val newsController = new NewsController(newsService, controllerComponents)

    lazy val router = new Routes(httpErrorHandler, homeController, newsController, assets)
  }
}

trait ReactiveMongoClient { self: BuiltInComponents =>
  lazy val mongoApi: ReactiveMongoApi = new DefaultReactiveMongoApi(configuration, applicationLifecycle)
}

trait MongoRepos { self: ReactiveMongoClient =>

  implicit def executionContext: ExecutionContext

  lazy val newsRepo: NewsRepo = new NewsMongoRepo {
    val mongoApi: ReactiveMongoApi = self.mongoApi
    val context: ExecutionContext = executionContext
  }
}

trait BusinessServices { self: MongoRepos =>

  implicit def executionContext: ExecutionContext

  lazy val newsService: NewsService = new NewsService {
    val repo: NewsRepo = self.newsRepo
    val context: ExecutionContext = executionContext
  }
}
