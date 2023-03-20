package watermark

import java.awt.Color
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.io.File
import java.lang.Exception
import javax.imageio.ImageIO
import kotlin.system.exitProcess


fun main() {
    // IMAGE FILENAME
    println("Input the image filename:")
    val filename = readln()
    val file = File(filename)
    fileExist(file)

    val myFile: BufferedImage = ImageIO.read(file)

    // CHECK COLOR COMPONENTS + PIXEL SIZE OF ORIGINAL FILE
    if (myFile.colorModel.numColorComponents != 3) {
        println("The number of image color components isn't 3.")
        exitProcess(0)
    } else if (myFile.colorModel.pixelSize != 24 && myFile.colorModel.pixelSize != 32) {
        println("The image isn't 24 or 32-bit.")
        exitProcess(0)
    }
    // WATERMARK FILENAME
    println("Input the watermark image filename:")
    val filenameWMark = readln()
    val fileWMark = File(filenameWMark)
    fileExist(fileWMark)

    val myFileWMark: BufferedImage = ImageIO.read(fileWMark)

    // CHECK COLOR COMPONENTS + PIXEL SIZE OF WATERMARK
        if (myFileWMark.colorModel.numColorComponents != 3) {
            println("The number of watermark color components isn't 3.")
            exitProcess(0)
        } else if (myFileWMark.colorModel.pixelSize != 24 && myFileWMark.colorModel.pixelSize != 32) {
            println("The watermark isn't 24 or 32-bit.")
            exitProcess(0)
        }

    // CHECKING SIZES
    if (myFileWMark.height > myFile.height || myFileWMark.width > myFile.width) {
        println("The watermark's dimensions are larger.")
        exitProcess(0)
    }

    // TRANSPARENCY COLOR
    var alpha = false
    var useTransparencyColor = false
    var transparencyColor = Color(0, 0, 0)
    if (myFileWMark.colorModel.transparency == 3) {
        println("Do you want to use the watermark's Alpha channel?")
        if (readln().equals("yes", ignoreCase = true)) {
            alpha = true
        }

    } else {
        println("Do you want to set a transparency color?")
        if (readln().equals("yes", ignoreCase = true)) {
            useTransparencyColor = true
        }
    }

    if (useTransparencyColor) {
        println("Input a transparency color ([Red] [Green] [Blue]):")
        val input = readln()
        if (Regex("\\d+ \\d+ \\d+").matches(input)) {
            val (red, green, blue) = input.split(" ").map { it.toInt() }
            if (red !in 0..255 || green !in 0..255 || blue !in 0..255) {
                print("The transparency color input is invalid.")
                exitProcess(0)
            } else {
                transparencyColor = Color(red, green, blue)
            }
        }
    }

    // TRANSPARENCY PERCENTAGE
    val transparencyInput = transparencyPercentage()

    // CHOOSE POSITION METHOD
    println("Choose the position method (single, grid):")
    val position = readln()
    var posX = 0
    var posY = 0
    var single = false

    //SINGLE + POSITION
    if (position == "single") {
        println("Input the watermark position ([x 0-${myFile.width - myFileWMark.width}] [y 0-${myFile.height - myFileWMark.height}]):")
        val positionXY = readln()
        if (positionXY.matches(Regex("-?\\d+ -?\\d+"))) {
            val positions = positionXY.split(" ").map { it.toInt() }
            posX = positions[0]
            posY = positions[1]
            single = true
            if (posX !in 0..(myFile.width - myFileWMark.width) || posY !in 0..(myFile.height - myFileWMark.height)) {
                println("The position input is out of range.")
                exitProcess(0)
            }
        } else {
            println("The position input is invalid.")
            exitProcess(0)
        }
        // GRID
    } else if (position == "grid") {

    } else {
        println("The position method input is invalid.")
        exitProcess(0)
    }


    // OUTPUT FILE NAME
    val outputName = outPutName()

    val myOutFile = BufferedImage(myFile.width, myFile.height, BufferedImage.TYPE_INT_RGB)

    val weight = transparencyInput.toInt()
    var nX = 0
    for (x in 0 until myFile.width) {
        var nY = 0
        for (y in 0 until myFile.height) {
            val i = Color(myFile.getRGB(x, y))
            // SINGLE
            if (single) {
                if (x in posX until myFileWMark.width + posX && y in posY until myFileWMark.height + posY) {

                    val w = Color(myFileWMark.getRGB(x - posX, y - posY), true)
                    if (alpha && w.alpha == 0) {
                        val color = Color(i.red, i.green, i.blue)
                        myOutFile.setRGB(x, y, color.rgb)
                    } else if (useTransparencyColor &&
                        w.red == transparencyColor.red &&
                        w.green == transparencyColor.green &&
                        w.blue == transparencyColor.blue
                    ) {
                        val color = Color(i.red, i.green, i.blue)
                        myOutFile.setRGB(x, y, color.rgb)
                    } else {
                        val color = Color(
                            (weight * w.red + (100 - weight) * i.red) / 100,
                            (weight * w.green + (100 - weight) * i.green) / 100,
                            (weight * w.blue + (100 - weight) * i.blue) / 100
                        )
                        myOutFile.setRGB(x, y, color.rgb)
                    }
                } else {
                    myOutFile.setRGB(x, y, i.rgb)
                }
            //GRID
            } else {
                if (x >= myFileWMark.width) {
                    nX = x / myFileWMark.width
                }
                if (y >= myFileWMark.height) {
                    nY = y / myFileWMark.height
                }

                val w = Color(myFileWMark.getRGB(x - (nX * myFileWMark.width), y - (nY * myFileWMark.height)), true)

                if (alpha && w.alpha == 0) {
                    val color = Color(i.red, i.green, i.blue)
                    myOutFile.setRGB(x, y, color.rgb)
                } else if (useTransparencyColor &&
                    w.red == transparencyColor.red &&
                    w.green == transparencyColor.green &&
                    w.blue == transparencyColor.blue
                ) {
                    val color = Color(i.red, i.green, i.blue)
                    myOutFile.setRGB(x, y, color.rgb)
                } else {
                    val color = Color(
                        (weight * w.red + (100 - weight) * i.red) / 100,
                        (weight * w.green + (100 - weight) * i.green) / 100,
                        (weight * w.blue + (100 - weight) * i.blue) / 100
                    )
                    myOutFile.setRGB(x, y, color.rgb)
                }

            }
        }
    }
    val outFile = File(outputName)
    val extension = outFile.extension
    ImageIO.write(myOutFile, extension, outFile)
    println("The watermarked image $outputName has been created.")
}

fun fileExist(fileName: File) {
    if (!fileName.exists()) {
        println("The file $fileName doesn't exist.")
        exitProcess(0)
    }
}

fun outPutName(): String {
    println("Input the output image filename (jpg or png extension):")
    val outputName = readln()
    val regex = ".jpg$|.png$".toRegex()
    if (!regex.containsMatchIn(outputName)) {
        println("The output file extension isn't \"jpg\" or \"png\".")
        exitProcess(0)
    }
    return outputName
}

fun transparencyPercentage():String {
    println("Input the watermark transparency percentage (Integer 0-100):")
    val transparencyInput = readln()

    if (transparencyInput.toIntOrNull() == null) {
        println("The transparency percentage isn't an integer number.")
        exitProcess(0)
    } else if (transparencyInput.toInt() !in 0..100) {
        println("The transparency percentage is out of range.")
        exitProcess(0)
    }
    return transparencyInput
}

fun checkNumCompsAndPixelSize(myFileWMark: BufferedImage) {
    if (myFileWMark.colorModel.numColorComponents != 3) {
        println("The number of watermark color components isn't 3.")
        exitProcess(0)
    } else if (myFileWMark.colorModel.pixelSize != 24 && myFileWMark.colorModel.pixelSize != 32) {
        println("The watermark isn't 24 or 32-bit.")
        exitProcess(0)
    }
}

/* PART 4
fun main() {
    try {
        var alphaChannel = false
        var transparency = false
        var transparencyColor = Color(0, 0, 0)

        println("Input the image filename:")
        val image = File(readln())
        if (checkImage(image, "image")) return

        println("Input the watermark image filename:")
        val watermark = File(readln())
        if (checkImage(watermark, "watermark")) return

        val myImage = ImageIO.read(image)
        val myWatermark = ImageIO.read(watermark)
        if (myImage.width != myWatermark.width || myImage.height != myWatermark.height) {
            return println("The image and watermark dimensions are different.")
        }

        if (myWatermark.transparency == Transparency.TRANSLUCENT) {
            println("Do you want to use the watermark's Alpha channel?")
            val choice = readln()
            alphaChannel = choice.equals("yes", true)
        }

        if (myWatermark.transparency != Transparency.TRANSLUCENT) {
            println("Do you want to set a transparency color?")
            val choice = readln()
            transparency = choice.equals("yes", true)
        }

        if (transparency) {
            println("Input a transparency color ([Red] [Green] [Blue]):")
            try {
                val colors = readln().split(" ").map { it.toInt() }
                if (colors.size != 3) throw Exception()
                transparencyColor = Color(colors[0], colors[1], colors[2])
            } catch (e: Exception) {
                println("The transparency color input is invalid.")
                return
            }
        }

        println("Input the watermark transparency percentage (Integer 0-100):")
        val weight = readln().let {
            if (it.matches("""\d+""".toRegex())) {
                it.toInt()
            } else {
                return println("The transparency percentage isn't an integer number.")
            }
        }.let { if (it in 0..100) it else return println("The transparency percentage is out of range.") }

        println("Input the output image filename (jpg or png extension):")
        val output = File(readln().let {
            if (it.contains(".png") || it.contains(".jpg")) {
                it
            } else {
                return println("The output file extension isn't \"jpg\" or \"png\".")
            }
        }).apply { if (!this.exists()) this.createNewFile() }

        val myOutput = BufferedImage(myImage.width, myImage.height, BufferedImage.TYPE_INT_RGB)

        for (x in 0 until myImage.width) {
            for (y in 0 until myImage.height) {
                val i = Color(myImage.getRGB(x, y))
                val w = Color(myWatermark.getRGB(x, y), alphaChannel)

                val color = Color(
                    (weight * w.red + (100 - weight) * i.red) / 100,
                    (weight * w.green + (100 - weight) * i.green) / 100,
                    (weight * w.blue + (100 - weight) * i.blue) / 100
                )

                if (alphaChannel) {
                    if (w.alpha == 0) {
                        myOutput.setRGB(x, y, Color(myImage.getRGB(x, y)).rgb)
                    } else {
                        myOutput.setRGB(x, y, color.rgb)
                    }
                } else {
                    if (transparency) {
                        if (w.rgb == transparencyColor.rgb) {
                            myOutput.setRGB(x, y, Color(myImage.getRGB(x, y)).rgb)
                        } else {
                            myOutput.setRGB(x, y, color.rgb)
                        }
                    } else {
                        myOutput.setRGB(x, y, color.rgb)
                    }
                }
            }
        }

        ImageIO.write(myOutput, output.name.substring(output.name.lastIndex - 2), output)
        println("The watermarked image ${output.path} has been created.")
    } catch (e: NullPointerException) {
        return println(e.message)
    }
}

fun checkImage(image: File, choice: String): Boolean {
    val name = if (choice == "image") "image" else "watermark"

    if (!image.exists()) {
        println("The file ${image.path} doesn't exist.")
        return true
    }
    val myImage = ImageIO.read(image)

    if (myImage.colorModel.numColorComponents != 3) {
        println("The number of $name color components isn't 3.")
        return true
    }

    if (myImage.colorModel.pixelSize != 24 && myImage.colorModel.pixelSize != 32) {
        println("The $name isn't 24 or 32-bit.")
        return true
    }

    return false
}

 PART 3
fun main() {
    println("Input the image filename:")
    val filename = readln()
    val file = File(filename)

    fileExist(file)

    val myFile: BufferedImage = ImageIO.read(file)

    if (myFile.colorModel.numColorComponents != 3) {
        println("The number of image color components isn't 3.")
        exitProcess(0)
    } else if (myFile.colorModel.pixelSize != 24 && myFile.colorModel.pixelSize != 32) {
        println("The image isn't 24 or 32-bit.")
        exitProcess(0)
    }

    println("Input the watermark image filename:")
    val filenameWMark = readln()
    val fileWMark = File(filenameWMark)

    fileExist(fileWMark)

    val myFileWMark: BufferedImage = ImageIO.read(fileWMark)

    if (myFileWMark.colorModel.numColorComponents != 3) {
        println("The number of watermark color components isn't 3.")
        exitProcess(0)
    } else if (myFileWMark.colorModel.pixelSize != 24 && myFileWMark.colorModel.pixelSize != 32) {
        println("The watermark isn't 24 or 32-bit.")
        exitProcess(0)
    }

    if (myFileWMark.width != myFile.width || myFileWMark.height != myFile.height) {
        println("The image and watermark dimensions are different.")
        exitProcess(0)
    }

    // Part 3 modifying
    var watermarkTransparency = false
    if (myFileWMark.colorModel.transparency == 3) {
        println("Do you want to use the watermark's Alpha channel?")
        if (readln().equals("yes", ignoreCase = true)) {
            watermarkTransparency = true
        }

    }

    println("Input the watermark transparency percentage (Integer 0-100):")
    val transparencyInput = readln()

    if (transparencyInput.toIntOrNull() == null) {
        println("The transparency percentage isn't an integer number.")
        exitProcess(0)
    } else if (transparencyInput.toInt() !in 0..100) {
        println("The transparency percentage is out of range.")
        exitProcess(0)
    }

    println("Input the output image filename (jpg or png extension):")
    val outputName = readln()
    val regex = ".jpg$|.png$".toRegex()
    if (!regex.containsMatchIn(outputName)) {
        println("The output file extension isn't \"jpg\" or \"png\".")
        exitProcess(0)
    }

    val myOutFile = BufferedImage(myFileWMark.width, myFileWMark.height, BufferedImage.TYPE_INT_RGB)
    val weight = transparencyInput.toInt()
    for (x in 0 until myFileWMark.width) {
        for (y in 0 until myFileWMark.height) {
            val i = Color(myFile.getRGB(x, y))
            var w = Color(myFileWMark.getRGB(x, y))
            // Part 3 modifying
            var color: Color
            if (watermarkTransparency) {
                w = Color(myFileWMark.getRGB(x, y), true)
                if (w.alpha == 0) {
                    color = Color(i.red,i.green,i.blue)
                } else {
                    color = Color(
                        (weight * w.red + (100 - weight) * i.red) / 100,
                        (weight * w.green + (100 - weight) * i.green) / 100,
                        (weight * w.blue + (100 - weight) * i.blue) / 100
                    )
                }
            } else {
                color = Color(
                    (weight * w.red + (100 - weight) * i.red) / 100,
                    (weight * w.green + (100 - weight) * i.green) / 100,
                    (weight * w.blue + (100 - weight) * i.blue) / 100
                )
            }

            myOutFile.setRGB(x, y, color.rgb)
        }
    }
    val outFile = File(outputName)
    val extension = outFile.extension
    ImageIO.write(myOutFile, extension, outFile)
    println("The watermarked image $outputName has been created.")

}

fun fileExist(fileName: File) {
    if (!fileName.exists()) {
        println("The file $fileName doesn't exist.")
        exitProcess(0)
    }
}


// PART 2

fun main() {
    println("Input the image filename:")
    val filename = readln()
    val file = File(filename)

    fileExist(file)

    val myFile: BufferedImage = ImageIO.read(file)

    if (myFile.colorModel.numColorComponents != 3) {
        println("The number of image color components isn't 3.")
        exitProcess(0)
    } else if (myFile.colorModel.pixelSize != 24 && myFile.colorModel.pixelSize != 32) {
        println("The image isn't 24 or 32-bit.")
        exitProcess(0)
    }

    println("Input the watermark image filename:")
    val filenameWMark = readln()
    val fileWMark = File(filenameWMark)

    fileExist(fileWMark)

    val myFileWMark: BufferedImage = ImageIO.read(fileWMark)

    if (myFileWMark.colorModel.numColorComponents != 3) {
        println("The number of watermark color components isn't 3.")
        exitProcess(0)
    } else if (myFileWMark.colorModel.pixelSize != 24 && myFileWMark.colorModel.pixelSize != 32) {
        println("The watermark isn't 24 or 32-bit.")
        exitProcess(0)
    }

    if (myFileWMark.width != myFile.width || myFileWMark.height != myFile.height) {
        println("The image and watermark dimensions are different.")
        exitProcess(0)
    }

    println("Input the watermark transparency percentage (Integer 0-100):")
    val transparencyInput = readln()

    if (transparencyInput.toIntOrNull() == null) {
        println("The transparency percentage isn't an integer number.")
        exitProcess(0)
    } else if (transparencyInput.toInt() !in 0..100) {
        println("The transparency percentage is out of range.")
        exitProcess(0)
    }

    println("Input the output image filename (jpg or png extension):")
    val outputName = readln()
    val regex = ".jpg$|.png$".toRegex()
    if (!regex.containsMatchIn(outputName)) {
        println("The output file extension isn't \"jpg\" or \"png\".")
        exitProcess(0)
    }

    val myOutFile = BufferedImage(myFileWMark.width,myFileWMark.height,BufferedImage.TYPE_INT_RGB)
    val weight = transparencyInput.toInt()
    for (x in 0 until myFileWMark.width) {
        for (y in 0 until myFileWMark.height) {
            val i = Color(myFile.getRGB(x, y))
            val w = Color(myFileWMark.getRGB(x, y))

            val color = Color(
                (weight * w.red + (100 - weight) * i.red) / 100,
                (weight * w.green + (100 - weight) * i.green) / 100,
                (weight * w.blue + (100 - weight) * i.blue) / 100
            )

            myOutFile.setRGB(x, y, color.rgb)
        }
    }
    val outFile = File(outputName)
    val extension = outFile.extension
    ImageIO.write(myOutFile, extension, outFile)
    println("The watermarked image $outputName has been created.")

}

fun fileExist(fileName: File){
    if (!fileName.exists()) {
        println("The file $fileName doesn't exist.")
        exitProcess(0)
    }
}


 PART 1
fun main() {
    println("Input the image filename:")
    val filename = readln()
    val file = File(filename)

    if (!file.exists()){
        println("The file $filename doesn't exist.")
    }
    else {
        val myFile: BufferedImage = ImageIO.read(file)
        println("Image file: $filename\n" +
                "Width: ${myFile.width}\n" +
                "Height: ${myFile.height}\n" +
                "Number of components: ${myFile.colorModel.numComponents}\n" +
                "Number of color components: ${myFile.colorModel.numColorComponents}\n" +
                "Bits per pixel: ${myFile.colorModel.pixelSize}")

        when (myFile.transparency) {
            1 -> println("Transparency: OPAQUE")
            2 -> println("Transparency: BITMASK")
            3 -> println("Transparency: TRANSLUCENT")
        }
    }
}
*/