package com.company.tripplanner.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.company.tripplanner.data.models.Trip
import java.io.File
import java.io.FileOutputStream

object Constants {
    const val APP_NAME = "Voyage AI Planner"
    const val SUPABASE_URL = "https://tuvkdaeedrgiisjjncct.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR1dmtkYWVlZHJnaWlzampuY2N0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODAyNDA1MDAsImV4cCI6MjA5NTgxNjUwMH0.5icbhmi-MN-caiQgWhv4nCa7Cx2rNZWhUNpRB4gw8Z0"
}

object Validators {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

object Helpers {
    fun openDestinationInGoogleMaps(context: Context, destination: String?) {
        val cleanDestination = destination?.trim()
        if (cleanDestination.isNullOrEmpty()) {
            Toast.makeText(context, "Destination name is empty.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val encodedDestination = Uri.encode(cleanDestination)
            val mapsUrl = "https://www.google.com/maps/search/?api=1&query=$encodedDestination"
            val mapsUri = Uri.parse(mapsUrl)

            val mapIntent = Intent(Intent.ACTION_VIEW, mapsUri).apply {
                setPackage("com.google.android.apps.maps")
            }

            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, mapsUri)
                context.startActivity(browserIntent)
            }
        } catch (e: Exception) {
            try {
                val encodedDestination = Uri.encode(cleanDestination)
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$encodedDestination"))
                context.startActivity(fallbackIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Unable to open Google Maps or browser.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

object PdfExporter {
    fun exportTripToPdf(context: Context, trip: Trip) {
        try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842

            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas

            val titlePaint = Paint().apply {
                color = Color.parseColor("#4A00E0")
                textSize = 20f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val subtitlePaint = Paint().apply {
                color = Color.parseColor("#6200EE")
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val headerPaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val bodyPaint = Paint().apply {
                color = Color.BLACK
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }

            val cardBgPaint = Paint().apply {
                color = Color.parseColor("#F5F5FB")
                style = Paint.Style.FILL
            }

            val linePaint = Paint().apply {
                color = Color.LTGRAY
                strokeWidth = 1f
            }

            var y = 40f

            // App Header
            canvas.drawText("AI-Based Travel Planner", 40f, y, titlePaint)
            y += 20f
            canvas.drawText("Voyage AI Travel Itinerary", 40f, y, subtitlePaint)
            y += 15f
            canvas.drawLine(40f, y, (pageWidth - 40).toFloat(), y, linePaint)
            y += 25f

            // Trip Summary Box
            val summaryTop = y
            val summaryBottom = y + 100f
            canvas.drawRoundRect(40f, summaryTop, (pageWidth - 40).toFloat(), summaryBottom, 12f, 12f, cardBgPaint)

            var sumY = summaryTop + 25f
            val col1X = 55f
            val col2X = 300f

            canvas.drawText("Destination: ${trip.name}", col1X, sumY, headerPaint)
            canvas.drawText("Travel Dates: ${trip.dates}", col2X, sumY, bodyPaint)
            sumY += 20f

            canvas.drawText("Travelers: ${trip.members} Person(s)", col1X, sumY, bodyPaint)
            canvas.drawText("Travel Pace: ${trip.travel_pace}", col2X, sumY, bodyPaint)
            sumY += 20f

            val currencySymbols = mapOf("USD" to "$", "EUR" to "€", "GBP" to "£", "INR" to "₹")
            val symbol = currencySymbols[trip.currency] ?: trip.currency
            canvas.drawText("Total Budget: $symbol ${trip.total_budget}", col1X, sumY, bodyPaint)
            if (trip.interests.isNotBlank()) {
                canvas.drawText("Interests: ${trip.interests}", col2X, sumY, bodyPaint)
            }

            y = summaryBottom + 30f

            // Day-wise Itinerary
            val sortedDays = trip.itinerary.keys.sortedBy { it.toIntOrNull() ?: 0 }

            for (dayKey in sortedDays) {
                val activities = trip.itinerary[dayKey] ?: emptyList()

                if (y + 60f > pageHeight - 50f) {
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    y = 40f
                }

                canvas.drawText("Day $dayKey Itinerary", 40f, y, subtitlePaint)
                y += 12f
                canvas.drawLine(40f, y, (pageWidth - 40).toFloat(), y, linePaint)
                y += 20f

                for (act in activities) {
                    if (y + 50f > pageHeight - 50f) {
                        pdfDocument.finishPage(page)
                        pageNumber++
                        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        y = 40f
                    }

                    val actTop = y
                    val actHeight = 45f
                    canvas.drawRoundRect(40f, actTop, (pageWidth - 40).toFloat(), actTop + actHeight, 8f, 8f, cardBgPaint)

                    val timeText = if (act.time.isNotBlank()) "[${act.time}] " else ""
                    val titleText = "$timeText${act.title}"
                    canvas.drawText(titleText, 52f, actTop + 18f, headerPaint)

                    val metaText = "${act.type} • ${act.category} | Cost: ${act.cost}"
                    canvas.drawText(metaText, 52f, actTop + 34f, bodyPaint)

                    y += actHeight + 10f
                }

                y += 15f
            }

            pdfDocument.finishPage(page)

            val cacheFolder = File(context.cacheDir, "pdf")
            cacheFolder.mkdirs()
            val safeName = trip.name.replace("[^a-zA-Z0-9]".toRegex(), "_")
            val pdfFile = File(cacheFolder, "Itinerary_${safeName}.pdf")
            val fos = FileOutputStream(pdfFile)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()

            val fileUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Export & Share Itinerary PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)

            Toast.makeText(context, "PDF generated successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to export PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
