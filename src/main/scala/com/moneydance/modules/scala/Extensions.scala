package com.moneydance.modules.scala

import java.time.{LocalDate, ZoneId}

import com.infinitekind.moneydance.model.Txn
import com.infinitekind.util.DateUtil

object Extensions {

  def mdIntDateToLocalDate(i: Int): LocalDate = DateUtil.convertIntDateToLong(i).toInstant.atZone(ZoneId.systemDefault()).toLocalDate

  def mdLongDateToLocalDate(l: Long): LocalDate = mdIntDateToLocalDate(DateUtil.convertLongDateToInt(l))

  implicit class TxnExtensions(val transaction: Txn) extends AnyVal {

    def getDateEnteredLD = mdLongDateToLocalDate(transaction.getDateEntered)

    def getDateLD = mdIntDateToLocalDate(transaction.getDateInt)

    def getTaxDateLD = mdIntDateToLocalDate(transaction.getTaxDateInt)

  }

  implicit class LocalDateExtensions(val date: LocalDate) extends AnyVal {

    // YY YYM MDD
    // 20161005 = 2016 * 10000 + 10 * 100 + 5

    def toIntDate: Int = date.getYear * 10000 + date.getMonthValue * 100 + date.getDayOfMonth

  }


}
