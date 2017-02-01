package com.imageintelligence.http4c.middleware

import argonaut._
import Argonaut._
import org.http4s._
import org.http4s.server.HttpMiddleware

case class JsonLogLine(req: Request, resp: Response)

object JsonLogLine {
  implicit def EncodeJsonLogLine: EncodeJson[JsonLogLine] = EncodeJson { a =>
    Json(
      "req" := Json(
        "method" := a.req.method.toString(),
        "uri" := a.req.uri.toString,
        "headers" := a.req.headers.toList.map(h => (h.name.toString, h.value)).toMap,
        "queryString" := a.req.queryString,
        "remoteUser" := a.req.remoteUser,
        "remoteHost" := a.req.remoteHost
      ),
      "resp" := Json(
        "code" := a.resp.status.code.toString,
        "headers" := a.resp.headers.toList.map(h => (h.name.toString, h.value)).toMap
      )
    )
  }
}

object LoggingMiddleware {

  def toLog(req: Request): Boolean = {
    val notHealth = req.pathInfo != "/health"
    val notStatus = req.pathInfo != "/status"
    val notOptions = req.method != Method.OPTIONS
    notHealth && notStatus && notOptions
  }

  def apply(log: (Request, Response) => Unit): HttpMiddleware = { service =>
    HttpService.lift { request =>
      service.run(request).map { response =>
        log(request, response)
        response
      }
    }
  }

  def jsonLoggingMiddleware(log: String => Unit): HttpMiddleware = {
    apply { case (req, resp) =>
      if (toLog(req)) {
        log(JsonLogLine(req, resp).asJson.nospaces)
      }
    }
  }

  def basicLoggingMiddleware(log: String => Unit): HttpMiddleware = {
    def floorCode(code: Int): Int = {
      (code / 100) * 100
    }

    apply { case (req, resp) =>
      val code = resp.status.code
      val defaultMsg = s"${resp.status.code} - ${req.method} ${req.uri.toString}"

      val msg = floorCode(code) match {
        case 100 => defaultMsg
        case 200 => defaultMsg
        case 300 => defaultMsg
        case 400 => s"${defaultMsg} - Req: ${req} - Resp: ${resp}"
        case 500 => s"${defaultMsg} - Req: ${req} - Resp: ${resp}"
      }
      if (toLog(req)) {
        log(msg)
      }
    }
  }

}
