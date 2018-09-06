package ts

import java.util.concurrent.TimeUnit

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val baseURL = "http://localhost:8001"
  val httpConf = http
    .baseURL(baseURL)
    .contentTypeHeader("application/json")

    //重复次数.repeat(2)
    //during(20) 时间范围内重复循环
    //測試資料來源為csv檔案,queue如果用完會出現Exception，如果你希望無限循環應該使用csv("alert_terms.csv").circular
    val csvFeeder = csv("user1.csv").circular


  val scn = scenario("MUYU").during(2) {
    // A scenario is a chain of requests and pauses
    feed(csvFeeder)
      .exec(http("Login-API")
        .post("/user/loginSession")
        .body(StringBody("""{"openId":"${openId}"}"""))
        .check(status.is(200))
      ).pause(1 microseconds)
      .exec(http("Qiao-API")
        .post("/user/qiao")
        .body(StringBody("""{"openId":"${openId}","score":1}"""))
        .check(status.is(200))
      ).pause(1 microseconds)
      .exec(http("Join-API")
        .post("/team/joinTeam")
        .body(StringBody("""{"openId":"${openId}","teamId":100}"""))
        .check(status.is(200))
      ).pause(1 microseconds)
      .exec(http("Rank-API")
        .post("/rank/getTeamRankList")
        .body(StringBody("""{"openId":"${openId}"}"""))
        .check(status.is(200))
      ).pause(1 microseconds)
  }
  setUp(scn.inject(atOnceUsers(15)).protocols(httpConf)).maxDuration(10 minutes)
}
