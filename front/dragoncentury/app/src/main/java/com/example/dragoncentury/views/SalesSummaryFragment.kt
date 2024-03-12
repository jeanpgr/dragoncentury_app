package com.example.dragoncentury.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.dragoncentury.R
import com.example.dragoncentury.customcomponents.DatePickerFragment
import com.example.dragoncentury.models.SalesSummaryModel
import com.example.dragoncentury.viewmodel.SalesSummaryViewModel
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.pdfview.PDFView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class SalesSummaryFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sales_summary, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SalesSummaryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private val salesSummaryViewModel: SalesSummaryViewModel by viewModels()

    private lateinit var editTxtDateDsdRV: EditText
    private lateinit var editTxtDateHstRV: EditText

    private lateinit var iconGoDoc: ImageView
    private lateinit var iconDownDoc: ImageView
    private lateinit var iconCloseDoc: ImageView

    private lateinit var salesSummaryPdf: PDFView
    private lateinit var nameUnique: String

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
            isAceptado ->
        if (isAceptado) Toast.makeText(requireContext(), "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show()
        else Toast.makeText(requireContext(), "PERMISOS DENEGADOS", Toast.LENGTH_SHORT).show()
    }

    private val FOLDER_SALES_SUMMARY = "/resumen_ventas_dragoncentury"

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTxtDateDsdRV = view.findViewById(R.id.editTxtDatDsdRV)
        editTxtDateHstRV = view.findViewById(R.id.editTxtDatHstRV)

        iconGoDoc = view.findViewById(R.id.iconGoDoc)
        iconDownDoc = view.findViewById(R.id.iconDownDoc)
        iconCloseDoc = view.findViewById(R.id.iconCloseDoc)

        salesSummaryPdf = view.findViewById(R.id.viewPdfResumVen)

        editTxtDateDsdRV.setOnClickListener {
            showDatePickerDialog(editTxtDateDsdRV)
        }

        editTxtDateHstRV.setOnClickListener {
            showDatePickerDialog(editTxtDateHstRV)
        }

        iconGoDoc.setOnTouchListener { viewic, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    animateIconDown(viewic)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    animateIconUp(viewic)
                    if (!editTxtDateDsdRV.text.toString().isNullOrBlank() && !editTxtDateHstRV.text.toString().isNullOrBlank()) {
                        getSalesSummary(view, editTxtDateDsdRV.text.toString(), editTxtDateHstRV.text.toString())
                    } else {
                        Toast.makeText(requireContext(), "Se requiere el rango de fechas", Toast.LENGTH_LONG).show()
                    }
                }
            }
            false
        }

        iconDownDoc.setOnTouchListener { viewic, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    animateIconDown(viewic)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    animateIconUp(viewic)
                    downloadPdf()
                }
            }
            false
        }

        iconCloseDoc.setOnTouchListener { viewic, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    animateIconDown(viewic)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    animateIconUp(viewic)
                    deletePdf(nameUnique)
                }
            }
            false
        }

    }

    private fun verificarPermisos(view: View, salesSummaryModel: SalesSummaryModel) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(requireContext(), "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show()
                generatePdfSalesSummary(salesSummaryModel)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                Snackbar.make(view, "Permisos necesarios para crear archivos PDFs", Snackbar.LENGTH_INDEFINITE).setAction(
                    "Aceptar"
                ) {
                    requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }.show()
            }
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun generatePdfSalesSummary(salesSummaryModel: SalesSummaryModel) {
        try {
            val dir = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), FOLDER_SALES_SUMMARY)
            val nameUnique = generateUniqueFileName(dir)
            this.nameUnique = nameUnique
            if (!dir.exists()) {
                dir.mkdirs() // Use mkdirs() to create nested directories if needed
                Toast.makeText(context, "Carpeta    creada", Toast.LENGTH_SHORT).show()
            }

            val file = File(dir, nameUnique)
            val fos = FileOutputStream(file)
            val document = Document()
            PdfWriter.getInstance(document, fos)

            document.open()

            headerSalesSummary(document)
            bodySalesSummary(salesSummaryModel, document)

            document.close()
            viewPdfSalesSummary(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: DocumentException) {
            e.printStackTrace()
        }
    }

    private fun viewPdfSalesSummary(file: File) {
        salesSummaryPdf.visibility = View.VISIBLE
        salesSummaryPdf.fromFile(file)
        salesSummaryPdf.isZoomEnabled = true
        salesSummaryPdf.show()

    }

    private fun downloadPdf() {
        salesSummaryPdf.visibility = View.GONE
        nameUnique = null.toString()
        Toast.makeText(requireContext(), "Reporte descargado correctamente", Toast.LENGTH_LONG).show()
    }

    private fun deletePdf(nameUnique: String) {
        salesSummaryPdf.visibility = View.GONE
        editTxtDateDsdRV.text = null
        editTxtDateHstRV.text = null
        val dir = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), FOLDER_SALES_SUMMARY)
        val file = File(dir, nameUnique)
        if (file.exists()) {
            file.delete()
            Toast.makeText(requireContext(), "PDF descartado", Toast.LENGTH_SHORT).show()
            this.nameUnique = null.toString()
        }
    }

    private fun headerSalesSummary(document: Document) {
        val logoImage = getResizedLogoImage()
        document.add(logoImage)
        document.add(Paragraph("\n"))
        document.add(Paragraph("\n"))
        document.add(Paragraph("\n"))
        document.add(Paragraph("\n"))
        val titleFont = Font(Font.FontFamily.UNDEFINED, 12f, Font.BOLD)
        val titlePhrase = Phrase("Resumen de Ventas y Gastos del ${editTxtDateDsdRV.text} al ${editTxtDateHstRV.text}", titleFont)
        val paragraphTitle = Paragraph(titlePhrase)
        paragraphTitle.alignment = Element.ALIGN_CENTER
        document.add(paragraphTitle)
        document.add(Paragraph("\n"))
        document.add(Paragraph("\n"))
        document.add(Paragraph("\n"))
    }

    private fun bodySalesSummary(salesSummaryModel: SalesSummaryModel, document: Document) {
        val titleFont = Font(Font.FontFamily.UNDEFINED, 12f, Font.BOLD)
        val titleNumRep = Phrase("Num. Reporte", titleFont)
        val titleNamesEnc = Phrase("Nombs Encargado", titleFont)
        val titleDate = Phrase("Fecha", titleFont)
        val titleTotalVueltas = Phrase("Total Vueltas", titleFont)
        val titleTotalCort = Phrase("Total Cortesías", titleFont)
        val titleTotalGastos = Phrase("Total Gastos", titleFont)
        val titleTotalVenta = Phrase("Total Venta", titleFont)

        val tableDetailSales = PdfPTable(7)
        tableDetailSales.addCell(titleNumRep)
        tableDetailSales.addCell(titleNamesEnc)
        tableDetailSales.addCell(titleDate)
        tableDetailSales.addCell(titleTotalVueltas)
        tableDetailSales.addCell(titleTotalCort)
        tableDetailSales.addCell(titleTotalGastos)
        tableDetailSales.addCell(titleTotalVenta)

        for (detailSalesSumm in salesSummaryModel.detailSalesList) {
            tableDetailSales.addCell(detailSalesSumm.idReport.toString())
            tableDetailSales.addCell(detailSalesSumm.nombUser + " " + detailSalesSumm.apellUser)
            tableDetailSales.addCell(detailSalesSumm.fecha)
            tableDetailSales.addCell(detailSalesSumm.totalVueltas.toString())
            tableDetailSales.addCell(detailSalesSumm.totalCortesias.toString())
            tableDetailSales.addCell(detailSalesSumm.totalGasto.toString())
            tableDetailSales.addCell(detailSalesSumm.totalVenta.toString())
        }

        document.add(tableDetailSales)
        document.add(Paragraph("\n"))

        val titleSumTotVenta = Phrase("SUMA TOTAL VENTA", titleFont)
        val titleSumTotGast = Phrase("SUMA TOTAL GASTOS", titleFont)
        val titleSumTotCort = Phrase("SUMA TOTAL CORTESÍAS", titleFont)

        val tableResumenVentasGastos = PdfPTable(2)

        tableResumenVentasGastos.addCell(titleSumTotCort)
        tableResumenVentasGastos.addCell(salesSummaryModel.sumTotCort.toString())
        tableResumenVentasGastos.addCell(titleSumTotGast)
        tableResumenVentasGastos.addCell("$" +salesSummaryModel.sumTotGast.toString())
        tableResumenVentasGastos.addCell(titleSumTotVenta)
        tableResumenVentasGastos.addCell("$" + salesSummaryModel.sumTotSales.toString())

        document.add(tableResumenVentasGastos)
    }

    private fun getSalesSummary(view: View, dateDsdRv: String, dateHstRv: String) {
        salesSummaryViewModel.getLiveDataSalesSumm().observe(viewLifecycleOwner, Observer {
            verificarPermisos(view, it[0])
        })
        salesSummaryViewModel.getSalesSummary(requireContext(), dateDsdRv, dateHstRv)
    }

    // Function to generate a unique filename with timestamp
    private fun generateUniqueFileName(dir: File, baseName: String = "Resumen"): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        var fileName = "$baseName-$timestamp.pdf"
        var count = 1
        while (File(dir, fileName).exists()) {
            fileName = "$baseName-$timestamp-$count.pdf"
            count++
        }
        return fileName
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getResizedLogoImage(): Image {
        val logoBitmap = (requireContext().resources.getDrawable(R.mipmap.img_logo) as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        logoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        val logoImage = Image.getInstance(byteArray)
        logoImage.scaleAbsolute(80f, 60f)
        logoImage.setAbsolutePosition(50f, 700f)
        return logoImage
    }


    //Función para mostrar el DatePicker
    private fun showDatePickerDialog(editTxtDate: EditText) {
        val datePicker = DatePickerFragment {day, month, year ->  onDateSelected(day, month, year, editTxtDate) }
        datePicker.show(childFragmentManager, "datePicker")
    }

    //Función para setear edittxt con las fechas seleccionadas
    private fun onDateSelected(day: Int, month: Int, year: Int, editTxtDate: EditText) {
        val monthReal = month + 1
        editTxtDate.setText("$year-$monthReal-$day")
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