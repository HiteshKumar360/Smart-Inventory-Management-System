import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class SalesEntry extends JFrame implements ActionListener {

    JTextField t1, t2;
    JButton b1, b2;

    static final Color BG_TOP       = new Color(245, 247, 255);
    static final Color BG_BTM       = new Color(230, 235, 252);
    static final Color CARD_FILL    = new Color(255, 255, 255, 245);
    static final Color ACCENT       = new Color(60,  200, 220); 
    static final Color TEXT_PRIMARY = new Color(25,  30,  60);
    static final Color TEXT_MUTED   = new Color(100, 115, 155);
    static final Color FIELD_BG     = new Color(245, 247, 255);
    static final Color FIELD_BORDER = new Color(200, 210, 235);
    static final Color FIELD_FOCUS  = new Color(60,  200, 220, 160);

    JLabel previewName, previewPrice, previewStock, previewTotal;

    SalesEntry() {
        setTitle("Sales Entry");
        setSize(440, 620);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel bg = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BTM);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                RadialGradientPaint rgp = new RadialGradientPaint(
                        new Point2D.Float(getWidth()/2f, getHeight()/3f),
                        getWidth() * 0.7f, new float[]{0f, 1f},
                        new Color[]{ new Color(60, 200, 220, 18), new Color(0, 0, 0, 0) });
                g2.setPaint(rgp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bg.setLayout(new GridBagLayout());

        JPanel card = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                for (int i = 8; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 5));
                    g2.fillRoundRect(-i/2, i, getWidth()+i, getHeight()+i/2, 22+i, 22+i);
                }

                g2.setColor(CARD_FILL);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

                GradientPaint strip = new GradientPaint(
                        0, 0,
                        new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 230),
                        getWidth(), 0,
                        new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 50));
                g2.setPaint(strip);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 20, 20));
                g2.fillRect(0, 0, getWidth(), 5);
                g2.setClip(null);

                GradientPaint tint = new GradientPaint(
                        0, 0, new Color(60, 200, 220, 10),
                        0, getHeight()/3, new Color(0, 0, 0, 0));
                g2.setPaint(tint);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        card.setPreferredSize(new Dimension(375, 530));
        card.setOpaque(false);

        try {
            ImageIcon salesIcon = new ImageIcon("images/sales_1.png");
            if (salesIcon.getIconWidth() > 0) {
                Image scaled = salesIcon.getImage().getScaledInstance(52, 52, Image.SCALE_SMOOTH);
                JLabel icon = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
                icon.setBounds(0, 22, 375, 56);
                card.add(icon);
            } else {
                addFallbackIcon(card);
            }
        } catch (Exception ex) {
            addFallbackIcon(card);
        }

        JLabel title = new JLabel("Sales Entry", SwingConstants.CENTER);
        title.setForeground(TEXT_PRIMARY);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(0, 82, 375, 26);
        card.add(title);

        JLabel subtitle = new JLabel("Enter product ID and quantity to record a sale", SwingConstants.CENTER);
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setBounds(0, 108, 375, 18);
        card.add(subtitle);

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

        t1 = createField(card, "PRODUCT ID", "Enter product ID", 142);
        t2 = createField(card, "QUANTITY",   "Enter quantity",   212);

        t1.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                loadProductPreview();
            }
        });

        t1.addActionListener(ev -> t2.requestFocus());
        t2.addActionListener(this);

        JPanel previewBox = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 200, 220, 12));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(new Color(60, 200, 220, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            }
        };
        previewBox.setBounds(30, 282, 315, 110);
        previewBox.setOpaque(false);

        JLabel previewTitle = new JLabel("PRODUCT PREVIEW");
        previewTitle.setForeground(new Color(60, 200, 220));
        previewTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        previewTitle.setBounds(10, 8, 200, 14);
        previewBox.add(previewTitle);

        previewName  = makePreviewLabel("Name:",     10, 28);
        previewPrice = makePreviewLabel("Price:",    10, 52);
        previewStock = makePreviewLabel("In Stock:", 10, 76);
        previewTotal = makePreviewLabel("Total:",   160, 52);

        previewBox.add(previewName);
        previewBox.add(previewPrice);
        previewBox.add(previewStock);
        previewBox.add(previewTotal);
        card.add(previewBox);

        t2.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateTotal();
            }
        });

        b1 = new JButton("Record Sale") {
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

                if (glow > 0) {
                    for (int i = 6; i > 0; i--) {
                        g2.setColor(new Color(60, 200, 220, (int)(glow * 15)));
                        g2.fillRoundRect(-i, -i/2, getWidth()+i*2, getHeight()+i, 14+i, 14+i);
                    }
                }

                Color c1 = new Color(
                        (int)(40  + glow * 20),
                        (int)(185 + glow * 15),
                        (int)(205 + glow * 15));
                Color c2 = new Color(
                        (int)(20  + glow * 20),
                        (int)(165 + glow * 15),
                        (int)(185 + glow * 15));
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(1, 1, getWidth()-2, getHeight()/2-2, 10, 10);

                g2.setColor(new Color(20, 160, 180, 150));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(new Color(0, 80, 90, 80));
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText()))/2 + 1,
                        (getHeight() - fm.getHeight())/2 + fm.getAscent() + 1);
                g2.setColor(Color.WHITE);
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText()))/2,
                        (getHeight() - fm.getHeight())/2 + fm.getAscent());
            }
        };
        b1.setBounds(35, 408, 145, 44);
        b1.setOpaque(false);
        b1.setContentAreaFilled(false);
        b1.setBorderPainted(false);
        b1.setFocusPainted(false);
        b1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b1.addActionListener(this);
        card.add(b1);

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
        b2.setBounds(195, 408, 145, 44);
        b2.setOpaque(false);
        b2.setContentAreaFilled(false);
        b2.setBorderPainted(false);
        b2.setFocusPainted(false);
        b2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b2.addActionListener(ev -> clearAll());
        card.add(b2);

        JLabel note = new JLabel("Stock will be automatically updated after sale", SwingConstants.CENTER);
        note.setForeground(new Color(150, 165, 200));
        note.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        note.setBounds(0, 462, 375, 16);
        card.add(note);

        bg.add(card);
        add(bg);
        setVisible(true);
    }

    JLabel makePreviewLabel(String key, int x, int y) {
        JLabel lbl = new JLabel(key + "  —");
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setBounds(x, y, 140, 18);
        return lbl;
    }

    double currentPrice = 0;
    int currentStock = 0;

    void loadProductPreview() {
        String idStr = t1.getText().trim();
        if (idStr.isEmpty() || idStr.equals("Enter product ID")) return;

        try {
            int id = Integer.parseInt(idStr);
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT name, price, quantity FROM products WHERE id=?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                currentPrice = rs.getDouble("price");
                currentStock = rs.getInt("quantity");

                previewName.setText("Name:  " + name);
                previewPrice.setText("Price:  ₹ " + String.format("%.2f", currentPrice));
                previewStock.setText("In Stock:  " + currentStock);
                previewName.setForeground(TEXT_PRIMARY);
                previewPrice.setForeground(TEXT_PRIMARY);
                previewStock.setForeground(currentStock < 5
                        ? new Color(220, 60, 60)
                        : new Color(60, 180, 110));
                updateTotal();
            } else {
                previewName.setText("Name:  Not found");
                previewPrice.setText("Price:  —");
                previewStock.setText("In Stock:  —");
                previewTotal.setText("Total:  —");
                previewName.setForeground(new Color(220, 60, 60));
                currentPrice = 0; currentStock = 0;
            }
        } catch (Exception ex) {
            // Invalid ID format — ignore
        }
    }

    void updateTotal() {
        try {
            int qty = Integer.parseInt(t2.getText().trim());
            double total = currentPrice * qty;
            previewTotal.setText("Total:  ₹ " + String.format("%.2f", total));
            previewTotal.setForeground(new Color(60, 180, 110));
        } catch (Exception ex) {
            previewTotal.setText("Total:  —");
            previewTotal.setForeground(TEXT_MUTED);
        }
    }

    void addFallbackIcon(JPanel card) {
        JLabel icon = new JLabel("₹", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 28));
        icon.setForeground(ACCENT);
        icon.setBounds(0, 22, 375, 56);
        card.add(icon);
    }

    void clearAll() {
        t1.setText("Enter product ID"); t1.setForeground(TEXT_MUTED);
        t2.setText("Enter quantity");   t2.setForeground(TEXT_MUTED);
        previewName.setText("Name:  —");
        previewPrice.setText("Price:  —");
        previewStock.setText("In Stock:  —");
        previewTotal.setText("Total:  —");
        previewName.setForeground(TEXT_MUTED);
        previewPrice.setForeground(TEXT_MUTED);
        previewStock.setForeground(TEXT_MUTED);
        previewTotal.setForeground(TEXT_MUTED);
        currentPrice = 0; currentStock = 0;
        t1.requestFocus();
    }

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

    public void actionPerformed(ActionEvent e) {
        String idStr  = t1.getText().trim();
        String qtyStr = t2.getText().trim();

        if (idStr.isEmpty() || idStr.equals("Enter product ID")) {
            showError("Please enter product ID."); return;
        }
        if (qtyStr.isEmpty() || qtyStr.equals("Enter quantity")) {
            showError("Please enter quantity."); return;
        }

        try {
            int id  = Integer.parseInt(idStr);
            int qty = Integer.parseInt(qtyStr);

            if (id  <= 0) { showError("Product ID must be greater than 0."); return; }
            if (qty <= 0) { showError("Quantity must be greater than 0.");   return; }

            Connection con = DBConnection.getConnection();

            PreparedStatement ps1 = con.prepareStatement(
                    "SELECT name, price, quantity FROM products WHERE id=?"
            );
            ps1.setInt(1, id);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                String name  = rs.getString("name");
                double price = rs.getDouble("price");
                int stock    = rs.getInt("quantity");

                if (qty > stock) {
                    showError("Only " + stock + " units available. You requested " + qty + ".");
                    return;
                }

                double total = price * qty;

                PreparedStatement ps2 = con.prepareStatement(
                        "INSERT INTO sales(product_name, qty, total) VALUES(?,?,?)"
                );
                ps2.setString(1, name);
                ps2.setInt(2, qty);
                ps2.setDouble(3, total);
                ps2.executeUpdate();

                PreparedStatement ps3 = con.prepareStatement(
                        "UPDATE products SET quantity=? WHERE id=?"
                );
                int newStock = stock - qty;
                ps3.setInt(1, newStock);
                ps3.setInt(2, id);
                ps3.executeUpdate();

                if (newStock < 5) {
                    FancyDialog.showSuccess(this,
                            name + " — Qty: " + qty + "  |  Total: ₹" + String.format("%.2f", total),
                            "Sale Recorded  ! Low Stock",
                            "Remaining stock is " + newStock + " — consider restocking soon.");
                } else {
                    FancyDialog.showSuccess(this,
                            name + " — Qty: " + qty + "  |  Total: ₹" + String.format("%.2f", total),
                            "Sale Recorded",
                            "Remaining stock: " + newStock + " units.");
                }

                clearAll();

            } else {
                showError("No product found with ID " + id + ".");
            }

        } catch (NumberFormatException ex) {
            showError("Product ID and Quantity must be valid numbers.");
        } catch (Exception ex) {
            showError("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    void showError(String msg) {
        FancyDialog.showError(this, msg,
                "Sale Failed",
                "Please check the details and try again.");
    }

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
