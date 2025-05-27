package util;

import javax.swing.*;
import java.awt.*;

public class ImageUtil {
    
    /**
     * Scales an image to fit within the specified dimensions while maintaining aspect ratio
     * @param imagePath Path to the image file
     * @param maxWidth Maximum width for the scaled image
     * @param maxHeight Maximum height for the scaled image
     * @return ImageIcon with properly scaled image, or null if image not found
     */
    public static ImageIcon getScaledImageIcon(String imagePath, int maxWidth, int maxHeight) {
        try {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            
            // Check if image was loaded successfully
            if (originalIcon.getIconWidth() <= 0 || originalIcon.getIconHeight() <= 0) {
                return null;
            }
            
            int originalWidth = originalIcon.getIconWidth();
            int originalHeight = originalIcon.getIconHeight();
            
            // Calculate scaling factor to maintain aspect ratio
            double scaleX = (double) maxWidth / originalWidth;
            double scaleY = (double) maxHeight / originalHeight;
            double scale = Math.min(scaleX, scaleY); // Use smaller scale to fit within bounds
            
            int newWidth = (int) (originalWidth * scale);
            int newHeight = (int) (originalHeight * scale);
            
            // Scale the image
            Image scaledImage = originalIcon.getImage().getScaledInstance(
                newWidth, newHeight, Image.SCALE_SMOOTH);
            
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Creates a placeholder image when the actual image is not available
     * @param width Width of the placeholder
     * @param height Height of the placeholder
     * @param text Text to display on the placeholder
     * @return ImageIcon with placeholder image
     */
    public static ImageIcon createPlaceholderIcon(int width, int height, String text) {
        // Create a simple placeholder image
        Image placeholder = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) placeholder.getGraphics();
        
        // Set background color
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, width, height);
        
        // Set border
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawRect(0, 0, width - 1, height - 1);
        
        // Set text
        g2d.setColor(new Color(100, 100, 100));
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int x = (width - textWidth) / 2;
        int y = (height - textHeight) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return new ImageIcon(placeholder);
    }
    
    /**
     * Gets a scaled aircraft image with no fallback
     * @param imagePath Path to the aircraft image
     * @param maxWidth Maximum width
     * @param maxHeight Maximum height
     * @return ImageIcon with scaled image or null if not found
     */
    public static ImageIcon getAircraftImage(String imagePath, int maxWidth, int maxHeight) {
        return getScaledImageIcon(imagePath, maxWidth, maxHeight);
    }
} 