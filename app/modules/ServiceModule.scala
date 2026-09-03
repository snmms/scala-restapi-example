// app/modules/ServiceModule.scala
package modules

import com.google.inject.AbstractModule
import services._
import repositories._

class ServiceModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[CategoriaService]).to(classOf[CategoriaServiceImpl])
    bind(classOf[ProductoService]).to(classOf[ProductoServiceImpl])
    bind(classOf[VentaService]).to(classOf[VentaServiceImpl])
    bind(classOf[CategoriaRepository]).to(classOf[CategoriaRepositoryImpl])
    bind(classOf[ProductoRepository]).to(classOf[ProductoRepositoryImpl])
    bind(classOf[VentaRepository]).to(classOf[VentaRepositoryImpl])
  }
}
