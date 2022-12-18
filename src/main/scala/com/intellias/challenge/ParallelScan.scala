package com.intellias.challenge

import common.parallel

import scala.annotation.tailrec

object ParallelScan extends ScanInterface {
  val zeroPosition = 0

  override def sequentialUpsweep(input: Array[Int], from: Int, until: Int): Int =
    findMaxDeclarative(input, from, until)

  @inline
  private def findMaxDeclarative(input: Array[Int], from: Int, until: Int): Int =
    input.slice(from, until).max

  // alternative imperative loop version that don't allocate temp sliced array
  @inline
  private def findMaxTailrec(input: Array[Int], from: Int, until: Int): Int = {
    @tailrec
    def findMaxRec(currentIndex: Int, currentMax: Int): Int = {
      if (currentIndex == until) currentMax
      else findMaxRec(currentIndex + 1, Math.max(currentMax, input(currentIndex)))
    }
    findMaxRec(from, input(from))
  }

  @inline
  private def findMaxImperative(input: Array[Int], fromIndex: Int, untilIndex: Int): Int = {
    var currentMax = input(fromIndex)
    for (i <- fromIndex until untilIndex) {
      val valueAtIndex = input(i)
      if (currentMax < valueAtIndex) {
        currentMax = valueAtIndex
      }
    }
    currentMax
  }

  override def upsweep(input: Array[Int], from: Int, until: Int, threshold: Int): Tree = {
    val rangeLength = until - from
    if (rangeLength <= threshold) {
      Tree.Leaf(from, until, maxForRange = sequentialUpsweep(input, from, until))
    } else {
      val middleOfTheRange = from + rangeLength / 2
      Tree(
        parallel(
          taskA = upsweep(input, from, middleOfTheRange, threshold),
          taskB = upsweep(input, middleOfTheRange, until, threshold)
        )
      )
    }
  }

  override def sequentialDownsweep(
    input: Array[Int],
    output: Array[Int],
    initialMaxValue: Int,
    from: Int,
    until: Int
  ): Unit = {
    @tailrec
    def downsweepRangeRec(currentIndex: Int, currentMax: Int): Unit = {
      if (currentIndex != until) {
        val maxOnCurrentPosition = Math.max(currentMax, input(currentIndex))
        output(currentIndex) = maxOnCurrentPosition
        downsweepRangeRec(currentIndex + 1, maxOnCurrentPosition)
      }
    }
    downsweepRangeRec(from, initialMaxValue)
  }

  override def downsweep(
    input: Array[Int],
    output: Array[Int],
    initialMaxValue: Int,
    tree: Tree
  ): Unit = {
    tree match {
      case Tree.Node(left, right) =>
        // TODO optimisation idea:
        //  if left.maxForRange <= initialMaxValue
        //     no need for recursive downsweep - we can fulfill whole range with initialMaxValue
        //  if right <= Math.max(left.maxForRange, initialMaxValue)
        //   no need for recursive downsweep - we can fulfill whole range with
        //   Math.max(left.maxForRange, initialMaxValue)
        parallel(
          taskA = downsweep(input, output, initialMaxValue, left),
          taskB = downsweep(input, output, Math.max(left.maxForRange, initialMaxValue), right)
        )
      case Tree.Leaf(from, until, maxForRange) =>
        sequentialDownsweep(input, output, initialMaxValue, from, until)
    }
  }

  override def scan(input: Array[Int], output: Array[Int], threshold: Int): Unit = {
    if (input.nonEmpty) {
      val inputAsTree = upsweep(input, zeroPosition, input.length, threshold)
      downsweep(input, output, initialMaxValue = input(zeroPosition), inputAsTree)
    }
  }

}
