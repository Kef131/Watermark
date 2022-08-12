package watermark

import java.awt.Color
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess


    private var widthImage = 0
    private var heightImage = 0
    private var widthWatermark = 0
    private var heightWatermark = 0
    private var diffX = 0
    private var diffY = 0
    private var originCoordinates = emptyList<Int>()
    private var transparencyWatermark = false
    private var transparencyColor: Color? = null

    private val BufferedImage.transparencyString
        get() = when (this.transparency) {
            Transparency.OPAQUE -> "OPAQUE"
            Transparency.BITMASK -> "BITMASK"
            Transparency.TRANSLUCENT -> "TRANSLUCENT"
            else -> "UNKNOWN"
        }

    fun imageInput(): BufferedImage {
        println("Input the image filename:")
        val imageFile = File(readln())
        var image : BufferedImage
        try {
            image = ImageIO.read(imageFile)
            if (image.colorModel.numColorComponents != 3) println("The number of image color components isn't 3.").also { exitProcess(-1) }
            if (image.colorModel.pixelSize !in 24..32) println("The image isn't 24 or 32-bit.").also { exitProcess(-1) }
            widthImage = image.width; heightImage = image.height
        } catch (e: Exception) {
            println("The file ${imageFile.path} doesn't exist.").also { exitProcess(-1) }
        }
        return image
    }

    private fun transparencyColorCheck(input: String): Color? {
        val colorInput = input.split(" ").map { it.toInt() }
        if (colorInput.size != 3)
            println("The transparency color input is invalid.").also { exitProcess(-1) }
        return try {
            colorInput.map {
                it.also { num ->
                    if (num !in 0..255)
                        println("The transparency color input is invalid.").also { exitProcess(-1) }
                }
            }
            Color(colorInput[0], colorInput[1], colorInput[2])
        } catch (e: Exception) {
            println("The transparency color input is invalid.").also { exitProcess(-1) }
            null
        }
    }

    fun watermarkInput(): BufferedImage {
        println("Input the watermark image filename:")
        val watermarkFile = File(readln())
        var watermarkImg: BufferedImage
        try {
            watermarkImg = ImageIO.read(watermarkFile)
            widthWatermark = watermarkImg.width
            heightWatermark = watermarkImg.height
            if (watermarkImg.colorModel.numColorComponents != 3) println("The number of watermark color components isn't 3.").also { exitProcess(-1) }
            if (watermarkImg.colorModel.pixelSize !in 24..32) println("The watermark isn't 24 or 32-bit.").also { exitProcess(-1) }
            if (widthWatermark >= widthImage || heightWatermark >= heightImage)
                println("The watermark's dimensions are larger.").also { exitProcess(-1) }
            if (watermarkImg.transparencyString == "TRANSLUCENT") {
                println("Do you want to use the watermark's Alpha channel?")
                readln().also { if (it == "yes") transparencyWatermark = true }
            } else {
                println("Do you want to set a transparency color?")
                readln().also {
                    if (it == "yes") {
                        println("Input a transparency color ([Red] [Green] [Blue]):")
                        transparencyColor = transparencyColorCheck(readln())
                    }
                }
            }

        } catch (e: Exception) {
            println("The file ${watermarkFile.path} doesn't exist.").also { exitProcess(-1) }
        }
        return watermarkImg
    }

    fun watermarkWeightInput(): Int {
        println("Input the watermark transparency percentage (Integer 0-100):")
        val watermarkWeight: Int
        try {
            watermarkWeight = readln().toInt().also { if (it !in 0..100) println("The transparency percentage is out of range.").also { exitProcess(-1) } }
        } catch (e: Exception) {
            println("The transparency percentage isn't an integer number.").also { exitProcess(-1) }
        }
        return watermarkWeight
    }

    fun positionMethod(): String {
        diffX = widthImage - widthWatermark
        diffY = heightImage - heightWatermark
        println("Choose the position method (single, grid):")
        val positionMethod = readln().also {
            when(it) {
                "single" -> {
                    try {
                        println("Input the watermark position ([x 0-$diffX] [y 0-$diffY]):")
                        originCoordinates = readln().split(" ").map { num -> num.toInt() }
                        if (originCoordinates.first() !in 0..diffX || originCoordinates.last() !in 0..diffY)
                            println("The position input is out of range.").also { exitProcess(-1) }
                    } catch (e: Exception) {
                        println("The position input is invalid.").also { exitProcess(-1) }
                    }
                }
                "grid" -> { }
                else -> println("The position method input is invalid.").also { exitProcess(-1) }
            }
        }
        return positionMethod
    }

    fun outputFileVerification(): String {
        println("Input the output image filename (jpg or png extension):")
        val outputFileName = readln().also {
            if (!it.contains(".jpg") && !it.contains(".png"))
                println("The output file extension isn't \"jpg\" or \"png\".").also { exitProcess(-1) }
        }
        return outputFileName
    }

    fun linearCombination(image: BufferedImage, watermarkImg: BufferedImage, watermarkWeight: Int, positionMethod: String, outputFileName: String) {
        val outputImage = image
        var color: Color
        var (x1, y1, x2, y2) = arrayOf(0, 0, 0, 0)
        if (positionMethod == "single") {
            x1 = originCoordinates.first()
            y1 = originCoordinates.last()
            x2 = x1 + watermarkImg.width
            y2 = y1 + watermarkImg.height

        } else {
            x1 = 0
            y1 = 0
            x2 = widthImage
            y2 = heightImage
        }

        for (x in x1 until x2) {
            for (y in y1 until y2) {
                val imageRGB = Color(image.getRGB(x, y))
                // In the area of single position do
                val watermarkRGB =
                    if (positionMethod == "single") {
                        if (transparencyWatermark) Color(watermarkImg.getRGB(x - originCoordinates[0], y - originCoordinates[1]), true)
                        else Color(watermarkImg.getRGB(x - originCoordinates[0], y - originCoordinates[1]))
                    } else {
                        if (transparencyWatermark) Color(watermarkImg.getRGB(x % widthWatermark, y % heightWatermark), true)
                        else Color(watermarkImg.getRGB(x % widthWatermark, y % heightWatermark))
                    }
                color = if (transparencyColor != null && watermarkRGB == transparencyColor
                    || transparencyWatermark && watermarkRGB.alpha == 0) {
                    Color(imageRGB.red, imageRGB.green, imageRGB.blue)
                } else {
                    Color(
                        (watermarkWeight * watermarkRGB.red + (100 - watermarkWeight) * imageRGB.red) / 100,
                        (watermarkWeight * watermarkRGB.green + (100 - watermarkWeight) * imageRGB.green) / 100,
                        (watermarkWeight * watermarkRGB.blue + (100 - watermarkWeight) * imageRGB.blue) / 100
                    )
                }
                outputImage.setRGB(x, y, color.rgb)
            }
        }
        ImageIO.write(outputImage, if(outputFileName.contains(".jpg")) "jpg" else "png", File(outputFileName))
        println("The watermarked image $outputFileName has been created.")
    }

fun main() = linearCombination(imageInput(), watermarkInput(), watermarkWeightInput(), positionMethod(), outputFileVerification())