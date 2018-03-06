package controllers

import conf.Configuration
import common._
import model._
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Json

import scala.concurrent._

class CampaignsController(
  val controllerComponents: ControllerComponents,
  val wsClient: WSClient,
)(implicit context: ApplicationContext)
  extends BaseController with ImplicitControllerExecutionContext with Logging {

  val root = Configuration.formstack.url + "/form/"
  val endpoint = "/submission.json"
  val token = Configuration.formstack.editorial.oAuthToken

  def formSubmit() = Action.async { implicit request: Request[AnyContent] =>

    println(request.body)

    val formId: String = request.body.asFormUrlEncoded.map(formData => formData("formId")).get.last
    val pageUrl: String = request.headers("referer")
    val jsonBody: Option[JsValue] = request.body.asFormUrlEncoded.map(formatData(_))

    jsonBody.map { json =>
      sendToFormstack(json, formId).flatMap { res =>
        println(res)
        if(res.status == 201) { Future.successful(Redirect(pageUrl)) }
        else { Future.failed( new Throwable("Sorry your story couldn't be sent"))} }
    }.getOrElse{ Future.failed( new Throwable("Sorry no data was sent")) }
  }

  private def formatData(formData: Map[String, Seq[String]]): JsValue = {
    val formKeys = formData.keys.filter(_ != "formId")
    val formattedData = formKeys.foldLeft(Map.empty[String, JsValue])((cleanData, key) => {
      val formValue: Option[JsValue] = formData get key map(a => {
        if (a.length == 1) JsString(a.last)
        else JsArray( a.map( b => JsString(b)))
      })
      cleanData + (key -> formValue.get)
    })
    Json.toJson(formattedData)
  }

  private def sendToFormstack(data: JsValue, formId: String): Future[WSResponse] = {
    wsClient.url(root + formId + endpoint).withHttpHeaders(
      "Authorization" -> s"Bearer $token",
      "Accept" -> "application/json",
      "Content-Type" -> "application/json"
    ).post(data)
  }

}
