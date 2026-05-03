import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class AddProduct extends JFrame implements ActionListener {

    JTextField t1, t2, t3, t4;
    JButton b1, b2;

    // ── Colors matching Dashboard ─────────────────────────────────────
    static final Color BG_TOP       = new Color(245, 247, 255);
    static final Color BG_BTM       = new Color(230, 235, 252);
    static final Color CARD_FILL    = new Color(255, 255, 255, 245);
    static final Color ACCENT       = new Color(82,  200, 140);
    static final Color TEXT_PRIMARY = new Color(25,  30,  60);
    static final Color TEXT_MUTED   = new Color(100, 115, 155);
    static final Color FIELD_BG     = new Color(245, 247, 255);
    static final Color FIELD_BORDER = new Color(200, 210, 235);
    static final Color FIELD_FOCUS  = new Color(82,  200, 140, 160);

    AddProduct() {
        setTitle("Add Product");
        setSize(440, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Background ────────────────────────────────────────────────
        JPanel bg = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BTM);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Subtle radial glow
                RadialGradientPaint rgp = new RadialGradientPaint(
                        new Point2D.Float(getWidth()/2f, getHeight()/3f),
                        getWidth() * 0.7f, new float[]{0f, 1f},
                        new Color[]{ new Color(82, 200, 140, 20), new Color(0, 0, 0, 0) });
                g2.setPaint(rgp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bg.setLayout(new GridBagLayout());

        // ── Card ──────────────────────────────────────────────────────
        JPanel card = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Drop shadow
                for (int i = 8; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 5));
                    g2.fillRoundRect(-i/2, i, getWidth()+i, getHeight()+i/2, 22+i, 22+i);
                }

                // White card
                g2.setColor(CARD_FILL);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

                // Top accent strip
                GradientPaint strip = new GradientPaint(
                        0, 0,
                        new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 230),
                        getWidth(), 0,
                        new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 50));
                g2.setPaint(strip);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 20, 20));
                g2.fillRect(0, 0, getWidth(), 5);
                g2.setClip(null);

                // Subtle top tint
                GradientPaint tint = new GradientPaint(
                        0, 0, new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 12),
                        0, getHeight()/3, new Color(0, 0, 0, 0));
                g2.setPaint(tint);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

                // Border
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        card.setPreferredSize(new Dimension(375, 500));
        card.setOpaque(false);

        // ── Header Icon ───────────────────────────────────────────────
        try {
            ImageIcon addIcon = new ImageIcon("images/add_1.png");
            if (addIcon.getIconWidth() > 0) {
                Image addScaled = addIcon.getImage().getScaledInstance(52, 52, Image.SCALE_SMOOTH);
                JLabel icon = new JLabel(new ImageIcon(addScaled), SwingConstants.CENTER);
                icon.setBounds(0, 22, 375, 56);
                card.add(icon);
            } else {
                // Fallback: draw icon circle with + symbol
                JLabel icon = new JLabel("+", SwingConstants.CENTER) {
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        int cx = getWidth()/2, cy = getHeight()/2;
                        g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(),
                                ACCENT.getBlue(), 30));
                        g2.fillOval(cx-26, cy-26, 52, 52);
                        g2.setColor(ACCENT);
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.drawOval(cx-26, cy-26, 52, 52);
                        super.paintComponent(g);
                    }
                };
                icon.setFont(new Font("Segoe UI", Font.BOLD, 28));
                icon.setForeground(ACCENT);
                icon.setBounds(0, 22, 375, 56);
                card.add(icon);
            }
        } catch (Exception ex) {
            JLabel icon = new JLabel("+", SwingConstants.CENTER);
            icon.setFont(new Font("Segoe UI", Font.BOLD, 28));
            icon.setForeground(ACCENT);
            icon.setBounds(0, 22, 375, 56);
            card.add(icon);
        }

        // ── Title ─────────────────────────────────────────────────────
        JLabel title = new JLabel("Add New Product", SwingConstants.CENTER);
        title.setForeground(TEXT_PRIMARY);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(0, 82, 375, 26);
        card.add(title);

        JLabel subtitle = new JLabel("Fill in the details below to add a product", SwingConstants.CENTER);
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setBounds(0, 108, 375, 18);
        card.add(subtitle);

        // Divider
        JPanel divider = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 0, 0, 0),
                        getWidth()/2, 0, ACCENT, true);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), 1);
            }
        };
        divider.setBounds(30, 132, 315, 1);
        divider.setOpaque(false);
        card.add(divider);

        // ── Fields ────────────────────────────────────────────────────
        t1 = createField(card, "PRODUCT NAME", "Enter product name", 142);
        t2 = createField(card, "CATEGORY",     "Enter category",     212);
        t3 = createField(card, "PRICE (₹)",    "Enter price",        282);
        t4 = createField(card, "QUANTITY",     "Enter quantity",     352);

        // Enter key navigation between fields
        t1.addActionListener(ev -> t2.requestFocus());
        t2.addActionListener(ev -> t3.requestFocus());
        t3.addActionListener(ev -> t4.requestFocus());
        t4.addActionListener(this);

        // ── Save Button ───────────────────────────────────────────────
        b1 = new JButton("Save Product") {
            boolean hovered = false;
            float glow = 0f;
            final Timer anim = new Timer(16, null);
            {
                anim.addActionListener(e -> {
                    glow += hovered ? 0.08f : -0.08f;
                    glow = Math.max(0f, Math.min(1f, glow));
                    repaint();
                });
                anim.start();
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true; }
                    public void mouseExited (MouseEvent e) { hovered = false; }
                });
            }
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Outer glow
                if (glow > 0) {
                    for (int i = 6; i > 0; i--) {
                        g2.setColor(new Color(82, 200, 140, (int)(glow * 15)));
                        g2.fillRoundRect(-i, -i/2, getWidth()+i*2, getHeight()+i, 14+i, 14+i);
                    }
                }

                // Gradient fill
                Color c1 = new Color(
                        (int)(60  + glow * 22),
                        (int)(180 + glow * 20),
                        (int)(120 + glow * 20));
                Color c2 = new Color(
                        (int)(40  + glow * 20),
                        (int)(160 + glow * 20),
                        (int)(100 + glow * 20));
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Shine
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(1, 1, getWidth()-2, getHeight()/2-2, 10, 10);

                // Border
                g2.setColor(new Color(60, 180, 120, 150));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);

                // Text
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                // Shadow
                g2.setColor(new Color(0, 80, 40, 80));
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText()))/2 + 1,
                        (getHeight() - fm.getHeight())/2 + fm.getAscent() + 1);
                g2.setColor(Color.WHITE);
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText()))/2,
                        (getHeight() - fm.getHeight())/2 + fm.getAscent());
            }
        };
        b1.setBounds(35, 428, 145, 44);
        b1.setOpaque(false);
        b1.setContentAreaFilled(false);
        b1.setBorderPainted(false);
        b1.setFocusPainted(false);
        b1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b1.addActionListener(this);
        card.add(b1);

        // ── Clear Button ──────────────────────────────────────────────
        b2 = new JButton("Clear") {
            boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                });
            }
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered
                        ? new Color(225, 230, 248)
                        : new Color(240, 243, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(180, 190, 220));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.setColor(TEXT_MUTED);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText()))/2,
                        (getHeight() - fm.getHeight())/2 + fm.getAscent());
            }
        };
        b2.setBounds(195, 428, 145, 44);
        b2.setOpaque(false);
        b2.setContentAreaFilled(false);
        b2.setBorderPainted(false);
        b2.setFocusPainted(false);
        b2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b2.addActionListener(ev -> clearFields());
        card.add(b2);

        bg.add(card);
        add(bg);
        setVisible(true);
    }

    // ── Clear Fields ──────────────────────────────────────────────────
    void clearFields() {
        t1.setText("Enter product name"); t1.setForeground(TEXT_MUTED);
        t2.setText("Enter category");     t2.setForeground(TEXT_MUTED);
        t3.setText("Enter price");        t3.setForeground(TEXT_MUTED);
        t4.setText("Enter quantity");     t4.setForeground(TEXT_MUTED);
        t1.requestFocus();
    }

    // ── Field Factory ─────────────────────────────────────────────────
    JTextField createField(JPanel parent, String labelText, String placeholder, int y) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setBounds(35, y, 305, 14);
        parent.add(lbl);

        JTextField field = new JTextField();
        field.setBounds(35, y + 16, 305, 42);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setCaretColor(ACCENT);
        field.setOpaque(true);
        field.setBackground(Color.WHITE);
        field.setText(placeholder);
        field.setForeground(TEXT_MUTED);
        field.setBorder(new RoundedFieldBorder(8, FIELD_BORDER, FIELD_BG));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
                field.setBorder(new RoundedFieldBorder(8, FIELD_FOCUS, FIELD_BG));
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_MUTED);
                }
                field.setBorder(new RoundedFieldBorder(8, FIELD_BORDER, FIELD_BG));
            }
        });

        parent.add(field);
        return field;
    }

    // ── Action Handler ────────────────────────────────────────────────
    public void actionPerformed(ActionEvent e) {
        String name     = t1.getText().trim();
        String category = t2.getText().trim();
        String priceStr = t3.getText().trim();
        String qtyStr   = t4.getText().trim();

        // Validation
        if (name.isEmpty() || name.equals("Enter product name")) {
            showError("Please enter product name."); return;
        }
        if (category.isEmpty() || category.equals("Enter category")) {
            showError("Please enter category."); return;
        }
        if (priceStr.isEmpty() || priceStr.equals("Enter price")) {
            showError("Please enter price."); return;
        }
        if (qtyStr.isEmpty() || qtyStr.equals("Enter quantity")) {
            showError("Please enter quantity."); return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int    qty   = Integer.parseInt(qtyStr);

            if (price <= 0) { showError("Price must be greater than 0."); return; }
            if (qty   <= 0) { showError("Quantity must be greater than 0."); return; }

            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO products(name, category, price, quantity) VALUES(?,?,?,?)"
            );
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            ps.setInt(4, qty);
            ps.executeUpdate();

            showSuccess("Product \"" + name + "\" added successfully!");
            clearFields();

        } catch (NumberFormatException ex) {
            showError("Price and Quantity must be valid numbers.");
        } catch (Exception ex) {
            showError("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ── Dialogs ───────────────────────────────────────────────────────
    void showError(String msg) {
        FancyDialog.showError(this, msg,
                "Add Product Failed",
                "Please check the product details.");
    }
    void showSuccess(String msg) {
        FancyDialog.showSuccess(this, msg,
                "Product Added",
                "Product has been saved to inventory.");
    }

    // ── Rounded Border for fields ─────────────────────────────────────
    static class RoundedFieldBorder extends AbstractBorder {
        private final int   radius;
        private final Color borderColor;
        private final Color fillColor;

        RoundedFieldBorder(int radius, Color border, Color fill) {
            this.radius      = radius;
            this.borderColor = border;
            this.fillColor   = fill;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, w-1, h-1, radius*2, radius*2);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }

        public boolean isBorderOpaque() { return false; }
    }
}