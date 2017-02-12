package com.moneydance.modules.scalamd

import java.time.LocalDate

import com.infinitekind.moneydance.model.{ParentTxn, SplitTxn, Txn}
import com.infinitekind.util.DateUtil

object Extensions {

  def mdIntDateToLocalDate(i: Int): LocalDate = {
    // ie: 20161008
    // YEAR: 20161008 / 10000 = 2016
    val year = i / 10000
    // MONTH: (20161008 / 100) % 100 = 201610 % 100 = 10
    val month = (i / 100) % 100
    // DAY: 20161008 % 100 = 8
    val day = i % 100 // = 20161008 % 100 = 08
    LocalDate.of(year, month, day)
    // NB: The following implementation seams more logical, but is proven to be buggy:
    // DateUtil.convertIntDateToLong(i).toInstant.atZone(ZoneId.systemDefault()).toLocalDate
  }

  def mdLongDateToLocalDate(l: Long): LocalDate = mdIntDateToLocalDate(DateUtil.convertLongDateToInt(l))

  implicit class TxnExtensions(val transaction: Txn) extends AnyVal {

    def getDateEnteredLD: LocalDate = mdLongDateToLocalDate(transaction.getDateEntered)

    def getDateLD: LocalDate = mdIntDateToLocalDate(transaction.getDateInt)

    def getTaxDateLD: LocalDate = mdIntDateToLocalDate(transaction.getTaxDateInt)

  }

  implicit class ParentTxnExtensions(val transaction: ParentTxn) extends AnyVal {

    def splits: Iterable[SplitTxn] = Iterator.tabulate(transaction.getSplitCount)(transaction.getSplit).toIterable

  }

  implicit def localDate2intDate(date: LocalDate): Int = {
    date.getYear * 10000 + date.getMonthValue * 100 + date.getDayOfMonth
  }

  implicit class LocalDateExtensions(val date: LocalDate) extends AnyVal {

    // YY YYM MDD
    // 20161005 = 2016 * 10000 + 10 * 100 + 5

    def toIntDate: Int = date.getYear * 10000 + date.getMonthValue * 100 + date.getDayOfMonth

  }

}
