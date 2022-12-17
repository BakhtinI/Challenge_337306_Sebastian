package com.intellias.challenge

import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.funsuite.AnyFunSuite

/** Using fact that sequential scan implementation is pretty straightforward and we have basic coverage that validate it correctness
  * we can write spec for ParallelScan where we validate that parallel version produce the same output like sequential one.
  */
class ParallelScanSpec extends UnitSpec with ScalaCheckPropertyChecks {

  "ParallelScan" should "produce the same results like SequentialScan for generated input array of ints" in {

    val genIntArray = Gen.containerOf[Array, Int](Gen.chooseNum(Int.MinValue, Int.MaxValue))

    forAll(genIntArray, minSuccessful(50)) { (provided: Array[Int]) =>
      println(s"Provided array: ${provided.mkString("Array(", ", ", ")")}")
      val sequentialScanOutput = new Array[Int](provided.length)
      SequentialScan.scan(provided, sequentialScanOutput)

      val parallelScanOutput = new Array[Int](provided.length)
      ParallelScan.scan(provided, sequentialScanOutput, 2)

      sequentialScanOutput shouldBe parallelScanOutput
    }
  }

}
