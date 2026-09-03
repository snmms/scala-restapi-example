package models

import play.api.libs.json._

case class Venta(
  id_venta: Long,
  fecha: String,
  total: Double
)

object Venta {
  implicit val format: Format[Venta] = Json.format[Venta]
}
