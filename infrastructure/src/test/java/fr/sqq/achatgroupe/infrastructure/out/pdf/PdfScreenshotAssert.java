package fr.sqq.achatgroupe.infrastructure.out.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.fail;

final class PdfScreenshotAssert {

    private static final Path REFERENCE_DIR = Path.of("src/test/resources/pdf-screenshots");
    private static final int DPI = 150;

    private PdfScreenshotAssert() {
    }

    static void assertPdfMatchesScreenshot(byte[] pdf, String baseName) throws IOException {
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();

            for (int i = 0; i < pageCount; i++) {
                String suffix = pageCount == 1 ? "" : "-page" + (i + 1);
                String fileName = baseName + suffix;
                BufferedImage actual = renderer.renderImageWithDPI(i, DPI);
                assertImageMatchesReference(actual, fileName);
            }
        }
    }

    private static void assertImageMatchesReference(BufferedImage actual, String fileName) throws IOException {
        Path referencePath = REFERENCE_DIR.resolve(fileName + ".png");
        Path actualPath = REFERENCE_DIR.resolve(fileName + "-actual.png");

        if (!Files.exists(referencePath)) {
            ImageIO.write(actual, "png", actualPath.toFile());
            fail("No reference image found at " + referencePath
                    + ". Actual image saved to " + actualPath
                    + ". Review it and rename (remove '-actual') to accept.");
        }

        BufferedImage expected = ImageIO.read(referencePath.toFile());

        if (expected.getWidth() != actual.getWidth() || expected.getHeight() != actual.getHeight()) {
            ImageIO.write(actual, "png", actualPath.toFile());
            fail("Image dimensions differ for " + fileName
                    + ": expected " + expected.getWidth() + "x" + expected.getHeight()
                    + " but got " + actual.getWidth() + "x" + actual.getHeight()
                    + ". Actual image saved to " + actualPath);
        }

        int[] expectedPixels = expected.getRGB(0, 0, expected.getWidth(), expected.getHeight(), null, 0, expected.getWidth());
        int[] actualPixels = actual.getRGB(0, 0, actual.getWidth(), actual.getHeight(), null, 0, actual.getWidth());

        for (int i = 0; i < expectedPixels.length; i++) {
            if (expectedPixels[i] != actualPixels[i]) {
                ImageIO.write(actual, "png", actualPath.toFile());
                fail("Pixel mismatch for " + fileName + " at pixel index " + i
                        + ". Actual image saved to " + actualPath
                        + ". Compare with reference at " + referencePath);
            }
        }
    }
}
