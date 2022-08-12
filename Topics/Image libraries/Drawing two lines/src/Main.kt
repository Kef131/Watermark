import java.awt.Color
import java.awt.image.BufferedImage

const val MAX = 200
fun drawLines(): BufferedImage {
    val image = BufferedImage(MAX, MAX, BufferedImage.TYPE_INT_RGB)
    val graphics = image.createGraphics()
    graphics.color = Color.red
    graphics.drawLine(0, 0, MAX, MAX)
    graphics.color = Color.green
    graphics.drawLine(MAX, 0, 0, MAX)
    return image
}