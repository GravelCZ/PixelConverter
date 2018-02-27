package cz.GravelCZLP.PixelDecoder;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

public class Main {

	public static void main(String[] args) {
		File f = new File("./files/");
		if (!f.exists()) {
			f.mkdir();
		}
		System.out.println("Starting...");
		if (args[0].equalsIgnoreCase("pngtobin")) {
			pngtobinary();
		} else if (args[0].equalsIgnoreCase("bintopng")) {
			bintopng();
		} else {
			System.out.println("pngtobin - converts .png (white and black only) file to binary text");
			System.out.println("bintopng - converts .txt file to .png fine (white and black only)");
		}
	}
	
	public static void pngtobinary() {
		File f = new File("./files/pngtobin.png");
		if (!f.exists()) {
			System.out.println("File not found.");
			System.exit(1);
		}
		BufferedImage img= null;
		try {
			img = ImageIO.read(f);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to read image.");
			System.exit(2);
		}
		
		StringBuffer binary = new StringBuffer();
		
		int[][] pixels = new int[img.getWidth()][img.getHeight()];
		
		System.out.println("Width: " + img.getWidth());
		System.out.println("Height: " + img.getHeight());
		
		System.out.println("Converting image to 2 dim int array");
		long start = System.currentTimeMillis();
		
		for (int h = 0; h < img.getHeight(); h++) {
			for (int w = 0; w < img.getWidth(); w++) {
				pixels[h][w] = img.getRGB(h, w);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Done. Process took: " + (end - start) + " ms.");
		
		System.out.println("Converting pixels to binary.");
		System.out.println("White = 1");
		System.out.println("Black = 0");
		long start1 = System.currentTimeMillis();
		for (int h = 0; h < pixels.length; h++) {
			for (int w = 0; w < pixels[h].length; w++) {
				int pixelAt = pixels[w][h];
				if (pixelAt == -1) { //white
					binary.append("1");
				} else if (pixelAt == -16777216) { //black
					binary.append("0");
				} else {
					try {
						throw new Exception("pixel is not white or black");
					} catch (Exception e) {
						e.printStackTrace();
					}	
				}
			}
		}
		long end1 = System.currentTimeMillis();
		System.out.println("Done. Process took: " + (end1 - start1) + " ms.");
		System.out.println("Output: " + binary.toString());
		System.out.println("Writing output to file (out.txt)");
		File out = new File("./out.txt");
		if (!out.exists()) {
			try {
				out.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed to create output file.");
				System.exit(3);
			}
		}
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(out));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to: " + e.getMessage());
			System.exit(4);
		}
		try {
			bw.write(binary.toString());
			bw.flush();
			bw.close();	
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(5);
		}
		System.exit(0);
	}
	
	public static void bintopng() {
		File f = new File("./files/bintopng.txt");
		if (!f.exists()) {
			System.out.println("File does not exist.");
			System.exit(1);
		}
		byte[] textBytes = null;
		System.out.println("Loading file to String.");
		long startLoad = System.currentTimeMillis();
		try {
			textBytes = Files.readAllBytes(f.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to read file with binary.");
			System.exit(2);
		}
		StringBuffer binary = new StringBuffer(new String(textBytes));
		long stopLoad = System.currentTimeMillis();
		System.out.println("Done, process took " + (stopLoad - startLoad) + " ms.");
		System.out.println("Loading String to Char array.");
		char[] binArray = binary.toString().toCharArray();
		
		int pixels = textBytes.length;
		System.out.println("Total binary values: " + pixels);
		System.out.println("Calculating ratio.");
		double squareRoot = Math.sqrt(pixels);
		int size = 0;
		if (squareRoot != (int) squareRoot) {
			squareRoot = squareRoot + 1;
			squareRoot = Math.floor(squareRoot);
		}
		
		size = (int) squareRoot;
		
		System.out.println("Done, final image will be " + size + " by " + size);
		
		System.out.println("Starting to convert binary file to image.");
		long convertStart = System.currentTimeMillis();
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		int nextPixel = 0;
		int charAt = 0;
		for (int h = 0; h < img.getHeight(); h++) {
			for (int w = 0; w < img.getWidth(); w++) {
				if (charAt >= binArray.length) {
					continue;
				}
				if (binArray[charAt] == '0') {
					nextPixel = -16777216;
				} else if (binArray[charAt] == '1') {
					nextPixel = -1;
				}
				charAt++;
				img.setRGB(w, h, nextPixel);
			}
		}
		long convertStop = System.currentTimeMillis();
		System.out.println("Done, process took " + (convertStop - convertStart) + " ms.");
		System.out.println("Writing file as out.png");
		File out = new File("./out.png");
		if (out.exists()) {
			out.delete();
		}
		try {
			out.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to create file.");
			System.exit(3);
		}
		try {
			ImageIO.write(img, "png", out);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to write to output file.");
			System.exit(4);
		}
		System.exit(0);
	}
}
