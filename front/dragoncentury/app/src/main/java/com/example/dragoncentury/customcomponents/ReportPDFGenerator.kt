package com.example.dragoncentury.customcomponents

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Environment
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.example.dragoncentury.R
import com.example.dragoncentury.models.ReportModel
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
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

class ReportPDFGenerator {

    companion object {

        private lateinit var reportPdf: PDFView
        private const val FOLDER = "/reportes_dragoncentury"

        private fun downloadPdf(context: Context) {
            Toast.makeText(context, "Reporte descargado correctamente", Toast.LENGTH_LONG).show()
        }

        private fun deletePdf(context: Context, nameUnique: String) {
            val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), FOLDER)
            val file = File(dir, nameUnique)
            if (file.exists()) {
                file.delete()
            } else {
                Toast.makeText(context, "El PDF no existe", Toast.LENGTH_SHORT).show()
            }
        }

        fun generatePDF(context: Context, reportModel: ReportModel, nameReport: String) {
            try {
                val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), FOLDER)
                val nameUnique = generateUniqueFileName(dir, nameReport, reportModel.idReporte.toString())

                if (!dir.exists()) {
                    dir.mkdirs() // Use mkdirs() to create nested directories if needed
                    Toast.makeText(context, "Carpeta creada", Toast.LENGTH_SHORT).show()
                }

                val file = File(dir, nameUnique)
                val fos = FileOutputStream(file)
                val document = Document()
                PdfWriter.getInstance(document, fos)

                document.open()

                document.add(Paragraph("\n"))
                document.add(Paragraph("\n"))
                document.add(Paragraph("\n"))

                val logoImage = getResizedLogoImage(context)

                document.add(logoImage)
                document.add(Paragraph("\n"))
                document.add(Paragraph("\n"))
                document.add(Paragraph("\n"))
                document.add(Paragraph("\n"))
                document.add(Paragraph("\n"))
                document.add(Paragraph("\n"))

                document.add(Paragraph("Num. Reporte: ${reportModel.idReporte}"))
                document.add(Paragraph("Nombre encargado: ${reportModel.nombsUser}"))
                document.add(Paragraph("Fecha generado: ${reportModel.fecha}"))
                document.add(Paragraph("\n"))

                val table = PdfPTable(4)
                table.addCell("Nomb. Coche")
                table.addCell("Lect. Inicial")
                table.addCell("Lect. Final")
                table.addCell("Total Vueltas")

                for (coche in reportModel.detalleCoches) {
                    table.addCell(coche.nombCoche)
                    table.addCell(coche.lecturaInicial.toString())
                    table.addCell(coche.lecturaFinal.toString())
                    table.addCell(coche.numVueltas.toString())
                }

                table.addCell("Total Vueltas")
                table.addCell("")
                table.addCell("")
                table.addCell(reportModel.totalVueltas.toString())

                document.add(table)

                document.add(Paragraph("\n"))

                document.add(Paragraph("Detalle Gasto: ${reportModel.descripNov}"))
                document.add(Paragraph("Gasto Total: $${reportModel.gastoTotal}"))
                document.add(Paragraph("Total Venta: $${reportModel.totalVenta}"))

                document.close()

                showDialogPdfView(context, file, nameUnique)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: DocumentException) {
                e.printStackTrace()
            }
        }

        // Function to generate a unique filename with timestamp
        private fun generateUniqueFileName(dir: File, baseName: String, idReport: String): String {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            var fileName = "$baseName-$idReport-$timestamp.pdf"
            var count = 1
            while (File(dir, fileName).exists()) {
                fileName = "$baseName-$idReport-$timestamp-$count.pdf"
                count++
            }
            return fileName
        }

        private fun showDialogPdfView(context: Context, file: File, nameUnique: String) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_pdf_preview)
            val window = dialog.window
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

            reportPdf = dialog.findViewById(R.id.viewPdf)
            reportPdf.fromFile(file)
            reportPdf.isZoomEnabled = true
            reportPdf.show()

            val btnCloseViewPdf: ImageView = dialog.findViewById(R.id.iconCloseViewPdf)
            val btnDownPdf: ImageView = dialog.findViewById(R.id.iconDownloadPdf)

            btnCloseViewPdf.setOnClickListener {
                deletePdf(context, nameUnique)
                dialog.dismiss()
            }

            btnDownPdf.setOnClickListener {
                downloadPdf(context)
                dialog.dismiss()
            }

            dialog.show()
        }

        fun getResizedLogoImage(context: Context): Image {
            val logoBitmap = (context.resources.getDrawable(R.mipmap.img_logo) as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            logoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            val logoImage = Image.getInstance(byteArray)
            logoImage.scaleAbsolute(130f, 110f)
            logoImage.setAbsolutePosition(50f, 700f)
            return logoImage
        }
    }

}