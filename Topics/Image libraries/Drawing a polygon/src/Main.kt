import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/*fun main() {
    val image = drawPolygon()
    val imageFile = File("myFirstImage.png")

    ImageIO.write(image,"png",imageFile)
    println(imageFile.absolutePath)
}*/
fun drawPolygon(): BufferedImage {
    // Add your code here
    val height: Int = 300
    val width: Int = 300
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val xPoints = intArrayOf(50,100,200,250,200,100)
    val yPoints = intArrayOf(150,250,250,150,50,50)
    val graphics = image.createGraphics()
    graphics.color = Color.YELLOW
    graphics.drawPolygon(xPoints, yPoints, 6)

    return image
}