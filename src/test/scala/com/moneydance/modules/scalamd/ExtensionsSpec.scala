package com.moneydance.modules.scalamd

import java.time.LocalDate

import org.scalatest.{FreeSpec, Matchers}

class ExtensionsSpec extends FreeSpec with Matchers {

  "mdIntDateToLocalDate" - {

    "parse 19960604" in {
      Extensions.mdIntDateToLocalDate(19960604) shouldEqual LocalDate.of(1996, 6, 4)
    }

    "parse 20161006" in {
      Extensions.mdIntDateToLocalDate(20161006) shouldEqual LocalDate.of(2016, 10, 6)
    }

    "parse 19011225" in {
      Extensions.mdIntDateToLocalDate(19011225) shouldEqual LocalDate.of(1901, 12, 25)
    }

    "parse 18001225" in {
      Extensions.mdIntDateToLocalDate(18001225) shouldEqual LocalDate.of(1800, 12, 25)
    }

    "parse 16001225" in {
      Extensions.mdIntDateToLocalDate(16001225) shouldEqual LocalDate.of(1600, 12, 25)
    }

    "parse 9990101" in {
      Extensions.mdIntDateToLocalDate(9990101) shouldEqual LocalDate.of(999, 1, 1)
    }

  }

  "mdLongDateToLocalDate" - {

    // todo: Write tests for this conversion method.

  }

}
