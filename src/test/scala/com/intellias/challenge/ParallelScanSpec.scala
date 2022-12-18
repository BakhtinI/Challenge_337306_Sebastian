package com.intellias.challenge

import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.AppendedClues.convertToClueful

/** Using fact that sequential scan implementation is pretty straightforward and we have basic coverage that validate it correctness
  * we can write spec for ParallelScan where we validate that parallel version produce the same output like sequential one.
  */
class ParallelScanSpec extends UnitSpec with ScalaCheckPropertyChecks {

  val intGenerator: Gen[Int] = Gen.chooseNum(Int.MinValue, Int.MaxValue)
  val bigArraysLength: Int = 1_000_000
  val genBigIntArrays: Gen[Array[Int]] = Gen.containerOfN[Array, Int](bigArraysLength, intGenerator)
  val genIntArrayOfDifferentSizes: Gen[Array[Int]] = Gen.containerOf[Array, Int](intGenerator)

  "ParallelScan" should "produce an array consisting of the maximum values to the left of a given index" in {

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
      // small threshold to perform many processing units for small input i.e. avoid situation when
      // ParallelScan will perform sequential scan because array length is smaller than threshold
      ParallelScan.scan(provided, output, threshold = 2)
      output shouldBe expected
    }
  }

  it should "produce the same results like SequentialScan for generated input arrays of ints" in {

    forAll(genIntArrayOfDifferentSizes, minSuccessful(50_000)) { (provided: Array[Int]) =>
      // uncomment below print if you want to see what input arrays are generated
      // println(s"Provided array: ${provided.mkString("Array(", ", ", ")")}")
      val sequentialScanOutput = new Array[Int](provided.length)
      SequentialScan.scan(provided, sequentialScanOutput)

      val parallelScanOutput = new Array[Int](provided.length)
      ParallelScan.scan(provided, parallelScanOutput, 2)

      sequentialScanOutput shouldBe parallelScanOutput withClue s"for provided array ${provided
        .mkString("Array(", ", ", ")")}"
    }
  }

  it should "produce the same results like SequentialScan for generated big input arrays of ints" in {
    forAll(genBigIntArrays, minSuccessful(100), workers(10)) { (provided: Array[Int]) =>
      // uncomment below print if you want to see what input arrays are generated
      // println(s"Provided array: ${provided.mkString("Array(", ", ", ")")}")
      val sequentialScanOutput = new Array[Int](provided.length)
      SequentialScan.scan(provided, sequentialScanOutput)

      val parallelScanOutput = new Array[Int](provided.length)
      ParallelScan.scan(provided, parallelScanOutput, 2)

      sequentialScanOutput shouldBe parallelScanOutput withClue s"for provided array ${provided
        .mkString("Array(", ", ", ")")}"
    }
  }

}
