package com.intellias.challenge

import org.scalameter.*
import org.scalameter.api.*
import org.scalameter.Measurer.*

object ParallelScanPerfSpec extends Bench.OfflineReport {

  // in case we would like to pass custom configuration we can define it as following:
  val customConfig: MeasureBuilder[Unit, Double] = config(
    KeyValue(exec.jvmflags -> List("-server", "-Xms5000m", "-Xmx5000m")),
  ).withWarmer(Warmer.Default())

  val increasedByHopSizeArrayGenerator: Gen[Array[Int]] = for {
    size <- Gen.range("input size")(from = 0, upto = 50_000_001, hop = 2_000_000)
  } yield (0 until size).toArray

  val exponentialSizeArrayGenerator: Gen[Array[Int]] = for {
    size <- Gen.exponential("input size")(from = 1, until = 50_000_001, factor = 5)
  } yield (0 until size).toArray

  val increasedByHopSizeArrayWithThresholdGenerator: Gen[(Array[Int], Int)] = for {
    size      <- Gen.range("input size")(from = 0, upto = 50_000_001, hop = 2_000_000)
    threshold <- Gen.exponential("threshold")(from = 10, until = 50_000_001, factor = 5)
  } yield ((0 until size).toArray, threshold)

  val exponentialSizeArrayWithThresholdGenerator: Gen[(Array[Int], Int)] = for {
    a         <- Gen.exponential("input size")(from = 1, until = 50_000_001, factor = 5)
    threshold <- Gen.exponential("threshold")(from = 10, until = 50_000_001, factor = 5)
  } yield ((0 until a).toArray, threshold)

  performance of "scan" in {
    measure method "parallel scan" in {
//      measure method "parallel scan" config (customConfig.ctx) in {
      using(increasedByHopSizeArrayWithThresholdGenerator) in { (providedArray, threshold) =>
        val output = new Array[Int](providedArray.length)
        ParallelScan.scan(providedArray, output, threshold)
      }
    }

    measure method "sequential scan" in {
//      measure method "sequential scan" config (customConfig.ctx) in {
      using(increasedByHopSizeArrayGenerator) in { providedArray =>
        val output = new Array[Int](providedArray.length)
        SequentialScan.scan(providedArray, output)
      }
    }
  }
}
