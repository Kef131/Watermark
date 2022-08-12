fun printColor(myImage: BufferedImage) {
    val (x, y) = readln().split(" ").map { it.toInt() }
    val color = Color(myImage.getRGB(x, y), true)
    print("${color.red} ${color.green} ${color.blue} ${color.alpha}")
}
