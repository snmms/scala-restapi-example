package filters

import javax.inject._
import play.api.http.HttpFilters
import play.filters.hosts.AllowedHostsFilter
import play.filters.headers.SecurityHeadersFilter

@Singleton
class Filters @Inject()(
  allowedHostsFilter: AllowedHostsFilter,
  securityHeadersFilter: SecurityHeadersFilter
) extends HttpFilters {
  def filters = Seq(allowedHostsFilter, securityHeadersFilter)
}
