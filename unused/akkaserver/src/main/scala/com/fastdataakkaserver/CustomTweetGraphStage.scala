package com.fastdataakkaserver

import akka.stream.scaladsl.Source
import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.stage.{AsyncCallback, GraphStage, GraphStageLogic, StageLogging}



object CustomTweetGraphStage extends GraphStage[SourceShape[IncomingMessage]]{

  //TODO: Implement Shape
  val out = Outlet[IncomingMessage]("twitterStream.out")
  override val shape: SourceShape[IncomingMessage] = SourceShape.of(out)



  //TODO:Implement Create Logic
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with StageLogging{

    val consumerCallBack: AsyncCallback[_] = ???
  }

//  Source.fromGraph() -- Might be what we need to construct a graph stage logic.

}