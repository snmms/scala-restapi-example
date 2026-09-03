package db

import javax.inject._
import java.sql.DriverManager
import scala.io.Source

@Singleton
class DatabaseManager @Inject()() {

  private val dbPath = sys.props.getOrElse("db.path", "store.db")
  private val url = s"jdbc:sqlite:$dbPath"

  Class.forName("org.sqlite.JDBC")
  private val connection = DriverManager.getConnection(url)
  connection.createStatement().execute("PRAGMA foreign_keys = ON")

  def getConnection: java.sql.Connection = connection

  def init(): Unit = {
    val stmt = connection.createStatement()
    val rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'")
    val tables = Iterator.continually(rs).takeWhile(_.next()).map(_.getString("name")).toSet

    if (!tables.contains("categorias")) {
      val source = Source.fromResource("seed.sql")
      val sql = source.mkString
      source.close()
      stmt.execute(sql)
    }
  }

  init()
}
