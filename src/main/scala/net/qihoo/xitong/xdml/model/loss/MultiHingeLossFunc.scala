package net.qihoo.xitong.xdml.model.loss

import net.qihoo.xitong.xdml.model.linalg.BLAS
import org.apache.spark.mllib.linalg.Vector


class MultiHingeLossFunc(numClasses: Int) extends MultiLossFunc {

  def gradientFromMargins(margins: Array[Double], label: Double): Array[Double] = {
    val labelMap = Array.fill(margins.length)(-1.0)
    labelMap(label.toInt) = 1.0
    val gradientFactors = Array.fill(margins.length)(0.0)
    for(ind <- margins.indices) {
      gradientFactors(ind) =
        if (1.0 > labelMap(ind) * margins(ind)) {
          -labelMap(ind)
        } else {
          0.0
        }
    }
    gradientFactors
  }

  //////////////////////////  with intercept  //////////////////////////////////

  def gradientWithLoss(data: Vector, label: Double,
                       weightsArr: Array[Vector],
                       interceptArr: Array[Double]): (Array[Double], Double) = {
    val margins = Array.fill(weightsArr.length)(0.0)
    for(ind <- margins.indices) {
      margins(ind) = BLAS.dot(weightsArr(ind), data) + interceptArr(ind)
    }
    val labelMap = Array.fill(margins.length)(-1.0)
    labelMap(label.toInt) = 1.0
    val gradientFactors = Array.fill(margins.length)(0.0)
    val losses = Array.fill(margins.length)(0.0)
    for(ind <- margins.indices) {
      if (1.0 > labelMap(ind) * margins(ind)) {
        gradientFactors(ind) = -labelMap(ind)
        losses(ind) = 1.0 - labelMap(ind) * margins(ind)
      } else {
        gradientFactors(ind) = 0.0
        losses(ind) = 0.0
      }
    }
    (gradientFactors, losses.sum)
  }

  //////////////////////////  without intercept  //////////////////////////////////

  def gradientWithLoss(data: Vector, label: Double,
                       weightsArr: Array[Vector]): (Array[Double], Double) = {
    val margins = Array.fill(weightsArr.length)(0.0)
    for(ind <- margins.indices) {
      margins(ind) = BLAS.dot(weightsArr(ind), data)
    }
    val labelMap = Array.fill(margins.length)(-1.0)
    labelMap(label.toInt) = 1.0
    val gradientFactors = Array.fill(margins.length)(0.0)
    val losses = Array.fill(margins.length)(0.0)
    for(ind <- margins.indices) {
      if (1.0 > labelMap(ind) * margins(ind)) {
        gradientFactors(ind) = -labelMap(ind)
        losses(ind) = 1.0 - labelMap(ind) * margins(ind)
      } else {
        gradientFactors(ind) = 0.0
        losses(ind) = 0.0
      }
    }
    (gradientFactors, losses.sum)
  }

}
