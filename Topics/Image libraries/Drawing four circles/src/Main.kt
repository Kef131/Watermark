import java.awt.Color
import java.awt.image.BufferedImage

@Suppress("MagicNumber")
fun drawCircles() = BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB)
    .apply {
        createGraphics()
            .apply { color = Color.RED; drawOval(50, 50, 100, 100) }
            .apply { color = Color.YELLOW; drawOval(50, 75, 100, 100) }
            .apply { color = Color.GREEN; drawOval(75, 50, 100, 100) }
            .apply { color = Color.BLUE; drawOval(75, 75, 100, 100) }
    }
