package test

import controllers.CampaignsController
import play.api.test.Helpers._
import org.scalatest.{BeforeAndAfterAll, DoNotDiscover, FlatSpec, Matchers}


@DoNotDiscover class CampaignsControllerTest
  extends FlatSpec
    with Matchers
    with ConfiguredTestSuite
    with BeforeAndAfterAll
    with WithMaterializer
    with WithTestWsClient
    with WithTestApplicationContext
    with WithTestContentApiClient {

    lazy val campaignsController = new CampaignsController(play.api.test.Helpers.stubControllerComponents(), wsClient)

  "Campaigns controller" should "201 when form submitted" in {
    val result = campaignsController.formSubmit()(TestRequest("/formstack-url-stub"))
    status(result) should be(201)
  }

  it should "get the form id correctly" in {

  }

  it should "convert the form data to json" in {

  }

}
