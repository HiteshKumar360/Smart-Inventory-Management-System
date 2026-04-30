import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Drop-in replacement for JOptionPane dialogs.
 * Usage:
 *   FancyDialog.showSuccess(parentFrame, "Welcome back, admin!");
 *   FancyDialog.showError(parentFrame, "Invalid username or password.");
 */
public class FancyDialog extends JDialog {

    // ── Palette (matches Login.java) ───────────────────────────────────
    static final Color BG          = new Color(22,  28,  56);
    static final Color BORDER_COL  = new Color(82, 153, 255, 46);
    static final Color TEXT_PRI    = new Color(232, 238, 248);
    static final Color TEXT_MUT    = new Color(160, 175, 210, 180);
    static final Color BTN_NORMAL  = new Color(255, 255, 255, 14);
    static final Color BTN_BORDER  = new Color(255, 255, 255, 26);
    static final Color SUCCESS_COL = new Color(90,  200, 140);
    static final Color ERROR_COL   = new Color(255, 100, 100);

    // ── Factory Methods ────────────────────────────────────────────────

    public static void showSuccess(JFrame parent, String message) {
        new FancyDialog(parent, "Login Successful", message,
                "You're now signed in to Smart Inventory.",
                SUCCESS_COL, "✔").setVisible(true);
    }

    public static void showError(JFrame parent, String message) {
        new FancyDialog(parent, "Authentication Failed", message,
                "Please check your credentials and try again.",
                ERROR_COL, "✕").setVisible(true);
    }

    // ── Constructor ────────────────────────────────────────────────────

    private FancyDialog(JFrame parent, String heading, String body,
                        String sub, Color accentColor, String iconText) {

        super(parent, true);
        setUndecorated(true);
        setSize(380, 220);
        setLocationRelativeTo(parent);
        setBackground(new Color(0, 0, 0, 0));
        getRootPane().setOpaque(false);

        // ── Root layer (rounded + shadow) ──────────────────────────────
        JLayeredPane root = new JLayeredPane();
        root.setPreferredSize(new Dimension(380, 220));
        root.setOpaque(false);

        // Card panel
        JPanel card = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Drop shadow
                for (int i = 14; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 7));
                    g2.fillRoundRect(-i, -i / 2, getWidth() + i * 2,
                            getHeight() + i, 28 + i, 28 + i);
                }

                // Card bg
                g2.setColor(BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);

                // Top accent bar (gradient)
                GradientPaint bar = new GradientPaint(
                        0, 0, accentColor,
                        getWidth(), 0,
                        new Color(accentColor.getRed(), accentColor.getGreen(),
                                accentColor.getBlue(), 80)
                );
                g2.setPaint(bar);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 22, 22));
                g2.fillRect(0, 0, getWidth(), 5);
                g2.setClip(null);

                // Card border
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            }
        };
        card.setOpaque(false);
        card.setBounds(0, 0, 380, 220);

        // ── Icon circle ────────────────────────────────────────────────
        JLabel iconLbl = new JLabel(iconText, SwingConstants.CENTER) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = new Color(accentColor.getRed(),
                        accentColor.getGreen(), accentColor.getBlue(), 30);
                g2.setColor(fill);
                g2.fillOval(0, 0, 50, 50);
                g2.setColor(new Color(accentColor.getRed(),
                        accentColor.getGreen(), accentColor.getBlue(), 100));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(0, 0, 49, 49);
                super.paintComponent(g);
            }
        };
        iconLbl.setForeground(accentColor);
        iconLbl.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
        iconLbl.setBounds(28, 32, 50, 50);
        card.add(iconLbl);

        // ── Heading label ──────────────────────────────────────────────
        JLabel tagLbl = new JLabel(heading.toUpperCase());
        tagLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tagLbl.setForeground(new Color(accentColor.getRed(),
                accentColor.getGreen(), accentColor.getBlue(), 200));
        tagLbl.setBounds(96, 32, 260, 16);
        card.add(tagLbl);

        JLabel bodyLbl = new JLabel("<html><body style='width:220px'>" + body + "</body></html>");
        bodyLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        bodyLbl.setForeground(TEXT_PRI);
        bodyLbl.setBounds(96, 50, 260, 22);
        card.add(bodyLbl);

        JLabel subLbl = new JLabel("<html><body style='width:220px'>" + sub + "</body></html>");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLbl.setForeground(TEXT_MUT);
        subLbl.setBounds(96, 76, 260, 36);
        card.add(subLbl);

        // ── Divider ────────────────────────────────────────────────────
        JPanel divider = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 18));
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        divider.setOpaque(false);
        divider.setBounds(24, 140, 332, 1);
        card.add(divider);

        // ── Dismiss button ─────────────────────────────────────────────
        JButton dismiss = createButton("Dismiss");
        dismiss.setBounds(170, 156, 90, 38);
        dismiss.addActionListener(e -> dispose());
        card.add(dismiss);

        // ── Continue button ────────────────────────────────────────────
        JButton cont = createAccentButton("Continue", accentColor);
        cont.setBounds(268, 156, 102, 38);
        cont.addActionListener(e -> dispose());
        card.add(cont);

        root.add(card, JLayeredPane.DEFAULT_LAYER);
        setContentPane(root);

        // Close on click outside
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

    // ── Button Helpers ─────────────────────────────────────────────────

    private JButton createButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? new Color(255, 255, 255, 22) : BTN_NORMAL;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(BTN_BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(new Color(190, 200, 220));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createAccentButton(String text, Color accentColor) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = getModel().isRollover()
                        ? accentColor.brighter() : accentColor;
                Color c2 = c1.darker();
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                // shine
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() / 2 - 2, 9, 9);
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
