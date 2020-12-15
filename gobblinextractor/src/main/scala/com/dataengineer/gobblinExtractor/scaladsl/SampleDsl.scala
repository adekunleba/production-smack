package com.dataengineer.gobblinExtractor.scaladsl

object SampleDsl {

  case class ClientAccount(no: String, name: String)

  val a1 = ClientAccount(no = "acc-123", name = "John J.")
  val a2 = ClientAccount(no = "acc-234", name = "Paul M.")
  val accounts = List(a1, a2)
  val newAccounts =
    ClientAccount(no = "acc-345", name = "Hugh P.") :: accounts
  newAccounts drop 1
}
