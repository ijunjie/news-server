package repos

import models.News
import reactivemongo.bson.{BSONDocument, BSONDocumentReader}
import validation.ValidationConstraints.ValidationResult

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
trait BSONReaders {
  implicit object NewsReader extends BSONDocumentReader[ValidationResult[News]] {
    override def read(bson: BSONDocument): ValidationResult[News] = {
      val id = bson.getAs[String]("_id")
      val title = bson.getAs[String]("title")
      val body = bson.getAs[String]("body")

      News(id, title, body)
    }
  }
}
