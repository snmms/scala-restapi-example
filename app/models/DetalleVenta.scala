package models

import play.api.libs.json._

case class DetalleVenta(
  id_detalle: Long,
  id_venta: Long,
  id_producto: Long,
  cantidad: Int,
  precio_unitario: Double
)

object DetalleVenta {
  implicit val format: Format[DetalleVenta] = Json.format[DetalleVenta]
}
