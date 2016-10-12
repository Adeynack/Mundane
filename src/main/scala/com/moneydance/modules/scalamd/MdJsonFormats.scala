package com.moneydance.modules.scalamd

import com.github.adeynack.scala.ToOption._
import com.infinitekind.moneydance.model.Account
import com.infinitekind.moneydance.model.Account.AccountType
import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, _}

import scala.collection.JavaConverters._


object MdJsonFormats {

  implicit val accountTypeWrites: Writes[AccountType] = (
    (__ \ 'name).write[String] ~
    (__ \ 'code).write[Int]
    ) { t: AccountType => (
    t.name(),
    t.code()
    )
  }

  implicit val accountWrites: Writes[Account] = (
    (__ \ 'name).write[String] ~
    (__ \ 'type).write[AccountType] ~
    (__ \ 'subAccounts).lazyWriteNullable(Writes.traversableWrites[Account])
    ) { a: Account => (
    a.getAccountName,
    a.getAccountType,
    a.getSubAccounts.asScala.noneIfEmpty
    )
  }

}
