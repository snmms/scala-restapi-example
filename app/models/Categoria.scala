package models

import play.api.libs.json._

case class Categoria(
  id_categoria: Long,
  nombre: String,
  descripcion: Option[String]
)

object Categoria {
  implicit val format: Format[Categoria] = Json.format[Categoria]
}
