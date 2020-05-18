package com.urdnot.iot.utils

import com.urdnot.iot.utils.messages.DataObjects

object HomeApiRoutes extends DataObjects {
  val service = "pdp-ui"
  val version = "v1"
//  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
//  def setupRoutes(): Route = {
//    pathPrefix(service / version) {
//      concat(
//        path("deploy") {
//          post {
//            implicit val timeout: Timeout = 5.seconds
//            // curl -d '{"environment": "stage", "image": "stage/jarjar:test_release"}' -H "Content-Type: application/json" -X POST http://localhost:8080/pdp-ui/v1/deploy
//            entity(as[DockerDeploy]) { dockerRequest: DockerDeploy =>
//              val deployDockerMessage: Future[DockerReply] = (system.actorOf(Props[DeployDocker]) ? dockerRequest).mapTo[DockerReply]
//              complete(deployDockerMessage)
//            }
//          }
//        },
//        pathPrefix("provision") {
//          concat(
//            path("hdfs") {
//              post {
//                implicit val timeout: Timeout = 5.seconds
//
//                // curl -d '{"environment": "latest", "paths": ["/test/path", "/test/path2"]}' -H "Content-Type: application/json" -X POST http://localhost:8080/pdp-ui/v1/provision/hdfs
//                entity(as[HdfsProvisionRequest]) { hdfsRequest: HdfsProvisionRequest =>
//                  val provisionHdfsMessage: Future[ProvisionHdfsResponse] = (system.actorOf(Props[ProvisionHdfs]) ? hdfsRequest).mapTo[ProvisionHdfsResponse]
//                  complete(provisionHdfsMessage)
//                  //TODO: resolve Future into Success and Failure and then run complete() on them each
//                }
//              }
//            }
//          )
//        }
//      )
//    }
//  }
}
