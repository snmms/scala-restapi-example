package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class CreateProductoRequest(
  nombre: String,
  descripcion: Option[String],
  precio: Double,
  stock: Int,
  stock_minimo: Int,
  id_categoria: Long
)

object CreateProductoRequest {
  implicit val reads: Reads[CreateProductoRequest] = (
    (__ \ "nombre").read[String](Reads.minLength[String](1)) and
    (__ \ "descripcion").readNullable[String] and
    (__ \ "precio").read[Double](Reads.min[Double](0.0)) and
    (__ \ "stock").read[Int](Reads.min[Int](0)) and
    (__ \ "stock_minimo").readWithDefault[Int](5) and
    (__ \ "id_categoria").read[Long]
  )(CreateProductoRequest.apply _)

  implicit val writes: Writes[CreateProductoRequest] = Json.writes[CreateProductoRequest]
}

case class UpdateProductoRequest(
  nombre: Option[String],
  descripcion: Option[String],
  precio: Option[Double],
  stock: Option[Int],
  stock_minimo: Option[Int],
  id_categoria: Option[Long]
)

object UpdateProductoRequest {
  implicit val reads: Reads[UpdateProductoRequest] = (
    (__ \ "nombre").readNullable[String](Reads.minLength[String](1)) and
    (__ \ "descripcion").readNullable[String] and
    (__ \ "precio").readNullable[Double](Reads.min[Double](0.0)) and
    (__ \ "stock").readNullable[Int](Reads.min[Int](0)) and
    (__ \ "stock_minimo").readNullable[Int](Reads.min[Int](0)) and
    (__ \ "id_categoria").readNullable[Long]
  )(UpdateProductoRequest.apply _)

  implicit val writes: Writes[UpdateProductoRequest] = Json.writes[UpdateProductoRequest]
}

case class CreateVentaRequest(
  items: Seq[CreateVentaItemRequest]
)

object CreateVentaRequest {
  implicit val reads: Reads[CreateVentaRequest] = Json.reads[CreateVentaRequest]
}

case class CreateVentaItemRequest(
  id_producto: Long,
  cantidad: Int
)

object CreateVentaItemRequest {
  implicit val reads: Reads[CreateVentaItemRequest] = Json.reads[CreateVentaItemRequest]
}

case class ProductoMasVendido(
  producto: Producto,
  total_vendido: Int
)

object ProductoMasVendido {
  implicit val format: Format[ProductoMasVendido] = Json.format[ProductoMasVendido]
}
