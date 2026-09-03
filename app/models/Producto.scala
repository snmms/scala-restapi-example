package models

import play.api.libs.json._

case class Producto(
  id_producto: Long,
  nombre: String,
  descripcion: Option[String],
  precio: Double,
  stock: Int,
  stock_minimo: Int,
  id_categoria: Long
)

object Producto {
  implicit val format: Format[Producto] = Json.format[Producto]
}
