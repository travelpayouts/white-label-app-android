package appconfig.parsers

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


object BgImageParser {

    private const val IMAGES_DIR = "config/images"
    private const val SCALED_IMG = "config/images/scaled.png"

    // config module dirs and files
    private const val RES_FOLDER = "src/main/res/"
    private const val IMG_FILENAME = "ta_img_default_background.png"
    private const val IMG_FILENAME_XML = "ta_img_default_background.xml"

    private const val DEFAULT_IMG_PATH = "src/main/res/drawable/ta_img_default_main_background.xml"

    private const val WIDTH = 1440
    private const val HEIGHT = 3216
    private const val RATIO = WIDTH.toFloat() / HEIGHT

    /**
     * Copy (and adjust if needed) background image for start fragments
     */
    fun parseImage(project: Project, configModule: Project) {
        print("Parsing images...      ")

        if (!project.file(IMAGES_DIR).exists()) {
            println("No 'images' directory. Skipping. ")
            return
        }

        val files = project.file(IMAGES_DIR).listFiles()

        val appModule = project.childProjects["app"] ?: throw IllegalStateException("config project not found")

        if (files.any { it.isXml() }) {
            val xmlDrawable = files.first { it.isXml() }
            xmlDrawable.copyTo(appModule.file(RES_FOLDER + "drawable/" + IMG_FILENAME_XML), true)

            Density.values().forEach {  density ->
                val toDelete = appModule.file(RES_FOLDER + density.dirName + "/" + IMG_FILENAME)
                if (toDelete.exists()) {
                    toDelete.delete()
                }
            }
        } else if (files.any { it.isPng() || it.isJpg() }) {
            val srcFile = files.first { it.isPng() || it.isJpg() }
            parseRasterImage(srcFile, appModule)

            val toDelete = appModule.file(RES_FOLDER + "drawable/" + IMG_FILENAME_XML)
            if (toDelete.exists()) {
                toDelete.delete()
            }
        } else {
            val defaultDrawable = appModule.file(DEFAULT_IMG_PATH)
            defaultDrawable.copyTo(appModule.file(RES_FOLDER + "drawable/" + IMG_FILENAME_XML), true)

            Density.values().forEach {  density ->
                val toDelete = appModule.file(RES_FOLDER + density.dirName + "/" + IMG_FILENAME)
                if (toDelete.exists()) {
                    toDelete.delete()
                }
            }
            print(" no image files to process. Skipping   ")
        }

        println("✅ ")
    }

    private fun parseRasterImage(srcFile: File, configModule: Project) {
        val image = ImageIO.read(srcFile)


        val srcWidth = image.width
        val srcHeight = image.height
        val srcRatio = srcWidth.toFloat() / srcHeight

        val subImage = if (srcRatio < RATIO) { // Image is narrow
            val adjusted = if (srcWidth < WIDTH) {
                val resizedHeight = ((srcHeight.toFloat() / srcWidth) * WIDTH).toInt()
                resizeImage(
                    image,
                    WIDTH,
                    resizedHeight
                )
            } else {
                image
            }

            adjusted.getSubimage(
                (adjusted.width / 2) - (WIDTH / 2),
                (adjusted.height / 2) - (HEIGHT / 2),
                WIDTH,
                HEIGHT
            )
        } else if (srcRatio > RATIO) { // Image is wider
            val adjusted = if (srcHeight < HEIGHT) {
                val resizedWidth = (HEIGHT * srcRatio).toInt()
                resizeImage(
                    image,
                    resizedWidth,
                    HEIGHT
                )
            } else {
                image
            }

            adjusted.getSubimage(
                (adjusted.width / 2) - (WIDTH / 2),
                (adjusted.height / 2) - (HEIGHT / 2),
                WIDTH,
                HEIGHT
            )
        } else {
            val adjusted = if (srcHeight < HEIGHT) {
                val resizedWidth = (HEIGHT * srcRatio).toInt()
                resizeImage(
                    image,
                    WIDTH,
                    resizedWidth
                )
            } else {
                image
            }

            adjusted.getSubimage(
                (adjusted.width / 2) - (WIDTH / 2),
                (adjusted.height / 2) - (HEIGHT / 2),
                WIDTH,
                HEIGHT
            )
        }

        Density.values().forEach { density ->
            val file = configModule.file(RES_FOLDER + density.dirName + "/" + IMG_FILENAME)

            file.ensureParentDirsCreated()

            if (density == Density.XXXHDPI) {
                ImageIO.write(subImage, "png", file)
            } else {
                val scaled = resizeImage(
                    subImage,
                    (subImage.width * density.scale).toInt(),
                    (subImage.height * density.scale).toInt()
                )

                ImageIO.write(scaled, "png", file)
            }

        }

    }

    private fun File.isXml(): Boolean {
        return extension.equals("xml", true)
    }

    private fun File.isPng(): Boolean {
        return extension.equals("png", true)
    }

    private fun File.isJpg(): Boolean {
        return extension.equals("jpg", true) || extension.equals("jpeg", true)
    }

    private fun resizeImage(
        originalImage: BufferedImage,
        targetWidth: Int,
        targetHeight: Int
    ): BufferedImage {
        val resizedImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        val graphics2D = resizedImage.createGraphics()
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null)
        graphics2D.dispose()
        return resizedImage
    }

    enum class Density(
        val dirName: String,
        val scale: Float
    ) {
        XXXHDPI("drawable-xxxhdpi", 1f),
        XXHDPI("drawable-xxhdpi", 0.75f),
        XHDPI("drawable-xhdpi", 0.5f),
        HDPI("drawable-xhdpi", 0.375f),
        MDPI("drawable-mdpi", 0.25f)

    }

}

