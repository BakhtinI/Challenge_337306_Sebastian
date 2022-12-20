package com.intellias.challenge

import org.scalameter.*
import org.scalameter.api.*
import org.scalameter.Measurer.*

object ParallelScanPerfSpec extends Bench.OfflineReport {

  val increasedByHopSizeArrayGenerator: Gen[Array[Int]] = for {
    size <- Gen.range("input size")(from = 0, upto = 50_000_001, hop = 2_000_000)
  } yield (0 until size).toArray

  val increasedByHopSizeArrayWithThresholdGenerator: Gen[(Array[Int], Int)] = for {
    size      <- Gen.range("input size")(from = 0, upto = 50_000_001, hop = 2_000_000)
    threshold <- Gen.exponential("threshold")(from = 10, until = 50_000_001, factor = 5)
  } yield ((0 until size).toArray, threshold)

  performance of "scan" in {
    measure method "parallel scan" in {
      using(increasedByHopSizeArrayWithThresholdGenerator) in { (providedArray, threshold) =>
        val output = new Array[Int](providedArray.length)
        ParallelScan.scan(providedArray, output, threshold)
      }
    }

    measure method "sequential scan" in {
      using(increasedByHopSizeArrayGenerator) in { providedArray =>
        val output = new Array[Int](providedArray.length)
        SequentialScan.scan(providedArray, output)
      }
    }
  }
}
