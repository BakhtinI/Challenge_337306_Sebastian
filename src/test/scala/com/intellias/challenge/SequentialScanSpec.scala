package com.intellias.challenge

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.prop.TableDrivenPropertyChecks

class SequentialScanSpec extends UnitSpec with TableDrivenPropertyChecks {

  "SequentialScan" should "produce an array consisting of the maximum values to the left of a given index" in {

    forAll(
      Table(
        ("provided", "expected"),
        (Array.empty[Int], Array.empty[Int]),
        (Array(-1), Array(-1)),
        (Array(-5, -4, -3, -2, -1, 0), Array(-5, -4, -3, -2, -1, 0)),
        (Array(0, -1, -2, -3, -4), Array(0, 0, 0, 0, 0)),
        (Array(0, 0, 1, 5, 2, 3, 6), Array(0, 0, 1, 5, 5, 5, 6)),
        (Array(0, -5, -100, 5, 2, 3, 6), Array(0, 0, 0, 5, 5, 5, 6))
      )
    ) { (provided, expected) =>
      val output = new Array[Int](provided.length)
      SequentialScan.scan(provided, output)
      output shouldBe expected
    }
  }

}
