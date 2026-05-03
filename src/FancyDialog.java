import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Premium dialog replacement for JOptionPane.
 *
 * Usage (Login — default messages):
 *   FancyDialog.showSuccess(parent, "Welcome back, admin!");
 *   FancyDialog.showError(parent, "Invalid username or password.");
 *
 * Usage (Other screens — custom heading + subtitle):
 *   FancyDialog.showSuccess(parent, "Product added!", "Product Added", "Product has been saved to inventory.");
 *   FancyDialog.showError(parent, "Please enter name.", "Add Product Failed", "Please check the product details.");
 */
public class FancyDialog extends JDialog {

    // ── Palette ────────────────────────────────────────────────────────
    static final Color BG          = new Color(22,  28,  56);
    static final Color BORDER_COL  = new Color(82, 153, 255, 46);
    static final Color TEXT_PRI    = new Color(232, 238, 248);
    static final Color TEXT_MUT    = new Color(160, 175, 210, 180);
    static final Color BTN_NORMAL  = new Color(255, 255, 255, 14);
    static final Color BTN_BORDER  = new Color(255, 255, 255, 26);
    static final Color SUCCESS_COL = new Color(90,  200, 140);
    static final Color ERROR_COL   = new Color(255, 100, 100);

    // ══════════════════════════════════════════════════════════════════
    // FACTORY METHODS — Login (default messages)
    // ══════════════════════════════════════════════════════════════════

    public static void showSuccess(JFrame parent, String message) {
        showSuccess(parent, message,
                "Login Successful",
                "You're now signed in to Smart Inventory.");
    }

    public static void showError(JFrame parent, String message) {
        showError(parent, message,
                "Authentication Failed",
                "Please check your credentials and try again.");
    }

    // ══════════════════════════════════════════════════════════════════
    // FACTORY METHODS — Custom heading + subtitle (all other screens)
    // ══════════════════════════════════════════════════════════════════

    public static void showSuccess(JFrame parent, String message,
                                   String heading, String subtitle) {
        applyLookAndFeel();
        new FancyDialog(parent, heading, message, subtitle,
                SUCCESS_COL, "✔").setVisible(true);
    }

    public static void showError(JFrame parent, String message,
                                 String heading, String subtitle) {
        applyLookAndFeel();
        new FancyDialog(parent, heading, message, subtitle,
                ERROR_COL, "✕").setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════
    // HELPER
    // ══════════════════════════════════════════════════════════════════

    private static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    // ══════════════════════════════════════════════════════════════════
    // CONSTRUCTOR
    // ══════════════════════════════════════════════════════════════════

    private FancyDialog(JFrame parent, String heading, String body,
                        String sub, Color accentColor, String iconText) {
        super(parent, true);
        setUndecorated(true);
        setSize(420, 250);
        setLocationRelativeTo(parent);
        setBackground(new Color(0, 0, 0, 0));
        getRootPane().setOpaque(false);

        JLayeredPane root = new JLayeredPane();
        root.setPreferredSize(new Dimension(420, 250));
        root.setOpaque(false);

        // ── Card ──────────────────────────────────────────────────────
        JPanel card = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Drop shadow
                for (int i = 14; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 7));
                    g2.fillRoundRect(-i, -i/2, getWidth()+i*2,
                            getHeight()+i, 28+i, 28+i);
                }

                // Card bg
                g2.setColor(BG);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 22, 22);

                // Top accent bar
                GradientPaint bar = new GradientPaint(
                        0, 0, accentColor,
                        getWidth(), 0,
                        new Color(accentColor.getRed(), accentColor.getGreen(),
                                accentColor.getBlue(), 80));
                g2.setPaint(bar);
                g2.setClip(new RoundRectangle2D.Float(
                        0, 0, getWidth(), getHeight(), 22, 22));
                g2.fillRect(0, 0, getWidth(), 5);
                g2.setClip(null);

                // Card border
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 22, 22);
            }
        };
        card.setOpaque(false);
        card.setBounds(0, 0, 420, 250);

        // ── Icon circle ───────────────────────────────────────────────
        JLabel iconLbl = new JLabel(iconText, SwingConstants.CENTER) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accentColor.getRed(),
                        accentColor.getGreen(), accentColor.getBlue(), 30));
                g2.fillOval(0, 0, 52, 52);
                g2.setColor(new Color(accentColor.getRed(),
                        accentColor.getGreen(), accentColor.getBlue(), 100));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(0, 0, 51, 51);
                super.paintComponent(g);
            }
        };
        iconLbl.setForeground(accentColor);
        iconLbl.setFont(new Font("Segoe UI Symbol", Font.BOLD, 20));
        iconLbl.setBounds(26, 28, 52, 52);
        card.add(iconLbl);

        // ── Heading tag ───────────────────────────────────────────────
        JLabel tagLbl = new JLabel(heading.toUpperCase());
        tagLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tagLbl.setForeground(new Color(accentColor.getRed(),
                accentColor.getGreen(), accentColor.getBlue(), 200));
        tagLbl.setBounds(96, 28, 300, 16);
        card.add(tagLbl);

        // ── Body message ──────────────────────────────────────────────
        JLabel bodyLbl = new JLabel(
                "<html><body style='width:270px'>" + body + "</body></html>");
        bodyLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bodyLbl.setForeground(TEXT_PRI);
        bodyLbl.setBounds(96, 48, 300, 42);
        card.add(bodyLbl);

        // ── Subtitle ──────────────────────────────────────────────────
        JLabel subLbl = new JLabel(
                "<html><body style='width:270px'>" + sub + "</body></html>");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setForeground(TEXT_MUT);
        subLbl.setBounds(96, 94, 300, 36);
        card.add(subLbl);

        // ── Divider ───────────────────────────────────────────────────
        JPanel divider = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 18));
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        divider.setOpaque(false);
        divider.setBounds(22, 154, 376, 1);
        card.add(divider);

        // ── Dismiss button ────────────────────────────────────────────
        JButton dismiss = createButton("Dismiss", false, accentColor);
        dismiss.setBounds(186, 166, 90, 38);
        dismiss.addActionListener(ev -> dispose());
        card.add(dismiss);

        // ── Continue button ───────────────────────────────────────────
        JButton cont = createButton("Continue", true, accentColor);
        cont.setBounds(288, 166, 110, 38);
        cont.addActionListener(ev -> dispose());
        card.add(cont);

        root.add(card, JLayeredPane.DEFAULT_LAYER);
        setContentPane(root);

        // Click outside to close
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { dispose(); }
        });

        // ESC to close
        getRootPane().registerKeyboardAction(
                ev -> dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    // ══════════════════════════════════════════════════════════════════
    // BUTTON HELPER
    // ══════════════════════════════════════════════════════════════════

    private JButton createButton(String text, boolean filled, Color accentColor) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (filled) {
                    Color c1 = getModel().isRollover()
                            ? accentColor.brighter() : accentColor;
                    GradientPaint gp = new GradientPaint(
                            0, 0, c1, 0, getHeight(), c1.darker());
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                    // Shine
                    g2.setColor(new Color(255, 255, 255, 35));
                    g2.fillRoundRect(1, 1, getWidth()-2, getHeight()/2-2, 9, 9);
                } else {
                    g2.setColor(getModel().isRollover()
                            ? new Color(255, 255, 255, 22) : BTN_NORMAL);
                    g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                    g2.setColor(BTN_BORDER);
                    g2.setStroke(new BasicStroke(0.8f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                }
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", filled ? Font.BOLD : Font.PLAIN, 12));
        btn.setForeground(filled ? Color.WHITE : new Color(190, 200, 220));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}