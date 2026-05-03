import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.RenderingHints;
import java.sql.*;

public class Login extends JFrame implements ActionListener {

    JTextField t1;
    JPasswordField t2;
    JButton b1;

    // ── Color Palette ──────────────────────────────────────────────────
    static final Color BG_TOP        = new Color(10,  12,  20);
    static final Color BG_BTM        = new Color(18,  24,  48);
    static final Color CARD_FILL     = new Color(22,  28,  52, 230);
    static final Color CARD_BORDER   = new Color(255, 255, 255, 18);
    static final Color ACCENT        = new Color(82, 153, 255);
    static final Color ACCENT_GLOW   = new Color(82, 153, 255, 60);
    static final Color ACCENT_HOVER  = new Color(120, 185, 255);
    static final Color FIELD_BG      = new Color(255, 255, 255, 12);
    static final Color FIELD_BORDER  = new Color(255, 255, 255, 30);
    static final Color FIELD_FOCUS   = new Color(82, 153, 255, 140);
    static final Color TEXT_PRIMARY  = new Color(230, 235, 255);
    static final Color TEXT_MUTED    = new Color(130, 145, 185);
    // ──────────────────────────────────────────────────────────────────

    Login() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        setTitle("Smart Inventory System");
        setSize(480, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(false);

        // ── Animated Background Panel ─────────────────────────────────
        BackgroundPanel bg = new BackgroundPanel();
        bg.setLayout(new GridBagLayout());

        // ── Card Panel ────────────────────────────────────────────────
        CardPanel card = new CardPanel();
        card.setPreferredSize(new Dimension(360, 450));
        card.setLayout(null);
        card.setOpaque(false);

        // ── Logo / Icon ───────────────────────────────────────────────
        ImageIcon rawIcon = new ImageIcon("images/logo.png"); // put logo.png in your project root folder
        Image scaled = rawIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        JLabel icon = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
        icon.setBounds(145, 28, 70, 70);
        card.add(icon);

        // ── Title ─────────────────────────────────────────────────────
        JLabel title = new JLabel("SMART INVENTORY", SwingConstants.CENTER);
        title.setForeground(TEXT_PRIMARY);
        title.setFont(loadFont("Segoe UI", Font.BOLD, 16));
        title.setBounds(30, 115, 300, 26);
        card.add(title);

        // Decorative accent line under title
        JSeparator sep = new JSeparator() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0,0,0,0),
                        getWidth()/2, 0, ACCENT,
                        true
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sep.setBounds(80, 145, 200, 2);
        sep.setOpaque(false);
        card.add(sep);

        JLabel subtitle = new JLabel("Sign in to your workspace", SwingConstants.CENTER);
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setBounds(30, 152, 300, 20);
        card.add(subtitle);

        // ── Username Field ────────────────────────────────────────────
        JLabel userLbl = new JLabel("USERNAME");
        userLbl.setForeground(TEXT_MUTED);
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        userLbl.setBounds(40, 195, 200, 16);
        card.add(userLbl);

        t1 = new JTextField();
        t1.setBounds(40, 214, 280, 44);
        styleField(t1);
        addPlaceholder(t1, "Enter your username");
        t1.addActionListener(this); // pressing Enter in username field triggers login
        card.add(t1);

        // ── Password Field ────────────────────────────────────────────
        JLabel passLbl = new JLabel("PASSWORD");
        passLbl.setForeground(TEXT_MUTED);
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        passLbl.setBounds(40, 272, 200, 16);
        card.add(passLbl);

        t2 = new JPasswordField();
        t2.setBounds(40, 291, 280, 44);
        styleField(t2);
        addPasswordPlaceholder(t2, "Enter your password");
        t2.addActionListener(this); // pressing Enter in password field triggers login
        card.add(t2);

        // ── Forgot Password ───────────────────────────────────────────
        JLabel forgot = new JLabel("Forgot password?");
        forgot.setForeground(ACCENT);
        forgot.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        forgot.setBounds(220, 340, 100, 16);
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.add(forgot);

        // ── Login Button ──────────────────────────────────────────────
        b1 = new GlowButton("Sign In");
        b1.setBounds(40, 368, 280, 48);
        b1.addActionListener(this);
        card.add(b1);

        // ── Footer ────────────────────────────────────────────────────
        JLabel footer = new JLabel("© 2026 Smart Inventory System", SwingConstants.CENTER);
        footer.setForeground(new Color(80, 95, 140));
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer.setBounds(30, 418, 300, 18);
        card.add(footer);

        bg.add(card);
        add(bg);
        setVisible(true);
    }

    // ── Field Styling ──────────────────────────────────────────────────
    void styleField(JTextField field) {
        field.setOpaque(false);
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new RoundedBorder(10, FIELD_BORDER, FIELD_BG));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(new RoundedBorder(10, FIELD_FOCUS, FIELD_BG));
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(new RoundedBorder(10, FIELD_BORDER, FIELD_BG));
            }
        });
    }

    void addPlaceholder(JTextField field, String text) {
        field.setForeground(TEXT_MUTED);
        field.setText(text);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(text)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(text);
                    field.setForeground(TEXT_MUTED);
                }
            }
        });
    }

    void addPasswordPlaceholder(JPasswordField field, String text) {
        field.setEchoChar((char) 0);
        field.setForeground(TEXT_MUTED);
        field.setText(text);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(text)) {
                    field.setText("");
                    field.setEchoChar('•');
                    field.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setText(text);
                    field.setEchoChar((char) 0);
                    field.setForeground(TEXT_MUTED);
                }
            }
        });
    }

    Font loadFont(String name, int style, int size) {
        return new Font(name, style, size);
    }

    // ── Action Handler ─────────────────────────────────────────────────
    public void actionPerformed(ActionEvent e) {
        String user = t1.getText().trim();
        String pass = new String(t2.getPassword());

        if (user.isEmpty() || user.equals("Enter your username")) {
            showError("Please enter your username.");
            return;
        }
        if (pass.isEmpty() || pass.equals("Enter your password")) {
            showError("Please enter your password.");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM users WHERE username=? AND password=?"
            );
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                showSuccess("Welcome back, " + user + "!");
                dispose();
                new Dashboard();
            } else {
                showError("Invalid username or password.");
            }
        } catch (Exception ex) {
            showError("Connection error. Please try again.");
            ex.printStackTrace();
        }
    }

    void showError(String msg) {
        FancyDialog.showError(this, msg);
    }

    void showSuccess(String msg) {
        FancyDialog.showSuccess(this, msg);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(Login::new);
    }

    // ══════════════════════════════════════════════════════════════════
    // Inner Classes
    // ══════════════════════════════════════════════════════════════════

    /** Animated starfield + gradient background */
    static class BackgroundPanel extends JPanel {
        private final int[][] stars;
        private final Timer timer;
        private float phase = 0;

        BackgroundPanel() {
            setOpaque(true);
            stars = new int[60][3]; // x, y, size
            java.util.Random rng = new java.util.Random();
            for (int[] s : stars) {
                s[0] = rng.nextInt(480);
                s[1] = rng.nextInt(600);
                s[2] = rng.nextInt(2) + 1;
            }
            timer = new Timer(50, e -> { phase += 0.02f; repaint(); });
            timer.start();
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background gradient
            GradientPaint gp = new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BTM);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Subtle radial glow at center
            RadialGradientPaint rgp = new RadialGradientPaint(
                    new Point2D.Float(getWidth() / 2f, getHeight() / 2f),
                    getWidth() * 0.6f,
                    new float[]{0f, 1f},
                    new Color[]{ new Color(40, 80, 160, 60), new Color(0, 0, 0, 0) }
            );
            g2.setPaint(rgp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Twinkling stars
            for (int[] s : stars) {
                float alpha = 0.3f + 0.5f * (float) Math.abs(Math.sin(phase + s[0] * 0.1f));
                g2.setColor(new Color(180, 210, 255, (int)(alpha * 255)));
                g2.fillOval(s[0], s[1], s[2], s[2]);
            }

            // Decorative corner geometry
            g2.setColor(new Color(82, 153, 255, 15));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(0, 60, 80, 0);
            g2.drawLine(getWidth(), getHeight() - 60, getWidth() - 80, getHeight());
            g2.setColor(new Color(82, 153, 255, 8));
            g2.drawLine(0, 100, 120, 0);
            g2.drawLine(getWidth(), getHeight() - 100, getWidth() - 120, getHeight());
        }
    }

    /** Frosted glass card with rounded corners and glow border */
    static class CardPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 24;
            // Shadow
            for (int i = 12; i > 0; i--) {
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(-i, -i/2, getWidth() + i*2, getHeight() + i, arc + i, arc + i);
            }
            // Card fill
            g2.setColor(CARD_FILL);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            // Card border
            g2.setColor(CARD_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            // Top highlight
            GradientPaint shine = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 22),
                    0, getHeight() / 3f, new Color(255, 255, 255, 0)
            );
            g2.setPaint(shine);
            g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() / 3, arc, arc);
        }
    }

    /** Glowing animated login button */
    static class GlowButton extends JButton {
        private float glowAlpha = 0f;
        private boolean hovering = false;
        private final Timer anim;

        GlowButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            anim = new Timer(16, e -> {
                glowAlpha += hovering ? 0.06f : -0.06f;
                glowAlpha = Math.max(0f, Math.min(1f, glowAlpha));
                repaint();
            });
            anim.start();

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovering = true; }
                public void mouseExited(MouseEvent e)  { hovering = false; }
            });
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 12;
            // Outer glow
            if (glowAlpha > 0) {
                for (int i = 8; i > 0; i--) {
                    g2.setColor(new Color(82, 153, 255, (int)(glowAlpha * 20)));
                    g2.fillRoundRect(-i, -i/2, getWidth() + i*2, getHeight() + i, arc + i*2, arc + i*2);
                }
            }
            // Button fill gradient
            Color c1 = blend(new Color(50, 120, 230), new Color(80, 160, 255), glowAlpha);
            Color c2 = blend(new Color(30,  90, 200), new Color(50, 130, 230), glowAlpha);
            GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            // Top shine
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() / 2 - 2, arc, arc);
            // Border
            g2.setColor(new Color(120, 180, 255, 120));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

            // Text
            FontMetrics fm = g2.getFontMetrics(getFont());
            int tx = (getWidth() - fm.stringWidth(getText())) / 2;
            int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            // Shadow
            g2.setColor(new Color(0, 30, 80, 100));
            g2.setFont(getFont());
            g2.drawString(getText(), tx + 1, ty + 1);
            // Label
            g2.setColor(getForeground());
            g2.drawString(getText(), tx, ty);
        }

        Color blend(Color a, Color b, float t) {
            return new Color(
                    (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t),
                    (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                    (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t)
            );
        }
    }

    /** Custom rounded border for text fields */
    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color borderColor;
        private final Color fillColor;

        RoundedBorder(int radius, Color border, Color fill) {
            this.radius = radius;
            this.borderColor = border;
            this.fillColor = fill;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillRoundRect(x, y, w - 1, h - 1, radius * 2, radius * 2);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius * 2, radius * 2);
        }

        public Insets getBorderInsets(Component c) { return new Insets(10, 15, 10, 15); }
        public boolean isBorderOpaque() { return false; }
    }
}