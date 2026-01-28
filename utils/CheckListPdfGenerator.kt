package com.example.tripshare.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.tripshare.ui.trip.CategoryUi
import java.io.File
import java.io.FileOutputStream

object ChecklistPdfGenerator {

    fun generatePdf(
        context: Context,
        tripName: String,
        categories: List<CategoryUi>
    ): File? {
        // 1. Setup Document
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        var yPosition = 50f
        val xMargin = 40f

        // 2. Draw Title
        paint.color = Color.BLACK
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("Checklist: $tripName", xMargin, yPosition, paint)
        yPosition += 40f

        // 3. Draw Content
        paint.textSize = 14f

        if (categories.isEmpty()) {
            paint.isFakeBoldText = false
            canvas.drawText("(No checklist items found)", xMargin, yPosition, paint)
        } else {
            categories.forEach { category ->
                // Safety check: Stop drawing if we run out of page space
                if (yPosition > 800) return@forEach

                // Category Header
                paint.isFakeBoldText = true
                paint.color = Color.BLUE
                yPosition += 20f
                canvas.drawText(category.title, xMargin, yPosition, paint)
                yPosition += 25f

                // Items
                paint.isFakeBoldText = false
                paint.color = Color.BLACK

                category.items.forEach { item ->
                    val status = if (item.completed) "[x]" else "[ ]"
                    val qty = if (item.quantity > 1) " (x${item.quantity})" else ""
                    val text = "$status ${item.title}$qty"

                    canvas.drawText(text, xMargin + 20f, yPosition, paint)
                    yPosition += 20f
                }
                yPosition += 10f
            }
        }

        pdfDocument.finishPage(page)

        // 4. Save to "pdfs" folder (Matches provider_paths.xml)
        val pdfFolder = File(context.filesDir, "pdfs")
        if (!pdfFolder.exists()) pdfFolder.mkdirs()

        // Create filename
        val fileName = "Checklist_${System.currentTimeMillis()}.pdf"
        val file = File(pdfFolder, fileName)

        return try {
            pdfDocument.writeTo(FileOutputStream(file))
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            pdfDocument.close()
        }
    }
}