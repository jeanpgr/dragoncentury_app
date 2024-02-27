package com.example.dragoncentury.adapters

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.example.dragoncentury.databinding.ItemViewReporteBinding
import com.example.dragoncentury.models.ReportModel

class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemViewReporteBinding.bind(view)

    @SuppressLint("ClickableViewAccessibility")
    fun render(reportModel: ReportModel, onClickListener: (ReportModel) -> Unit) {
        binding.txtNumReport.text = reportModel.idReporte.toString()
        binding.txtFechaReport.text = reportModel.fecha
        binding.txtTotalVueltas.text = reportModel.totalVueltas.toString()
        binding.txtTotalGastos.text = reportModel.gastoTotal.toString()
        binding.txtTotalVenta.text = reportModel.totalVenta.toString()

        binding.iconOpenFile.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> animateIconDown(view)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    animateIconUp(view)
                    onClickListener(reportModel)
                }
            }
            false
        }
    }

    private fun animateIconDown(view: View) {
        // Escalar la imagen hacia abajo
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.8f)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f)
        scaleDownX.duration = 200
        scaleDownY.duration = 200
        scaleDownX.interpolator = AccelerateDecelerateInterpolator()
        scaleDownY.interpolator = AccelerateDecelerateInterpolator()
        val scaleDown = AnimatorSet()
        scaleDown.play(scaleDownX).with(scaleDownY)
        scaleDown.start()
    }

    private fun animateIconUp(view: View) {
        // Escalar la imagen hacia arriba
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 1f)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 1f)
        scaleUpX.duration = 200
        scaleUpY.duration = 200
        scaleUpX.interpolator = AccelerateDecelerateInterpolator()
        scaleUpY.interpolator = AccelerateDecelerateInterpolator()
        val scaleUp = AnimatorSet()
        scaleUp.play(scaleUpX).with(scaleUpY)
        scaleUp.start()
    }
}