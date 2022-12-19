package com.intellias.challenge

enum Tree(val maxForRange: Int):
  case Node(left: Tree, right: Tree) extends Tree(left.maxForRange.max(right.maxForRange))
  case Leaf(from: Int, until: Int, override val maxForRange: Int) extends Tree(maxForRange)

object Tree:
  def apply(tuple: (Tree, Tree)): Tree = Node(tuple._1, tuple._2)
