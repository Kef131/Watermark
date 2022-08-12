import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

val height: Int = 800
val width: Int = 600

val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
val imageFile = File("myFirstImage.png")

fun saveImage(image: BufferedImage, imageFile: File) {
    ImageIO.write(image, "png", imageFile)
}
fun main(args: Array<String>) {
    val imageFile = File("myFirstImage.png")
    val image: BufferedImage = ImageIO.read(imageFile)

    val graphics = image.createGraphics()
    graphics.color = Color.RED
    graphics.drawPolygon(intArrayOf(10, 20, 30), intArrayOf(100, 20, 100), 3)
    saveImage(image, imageFile)

}
