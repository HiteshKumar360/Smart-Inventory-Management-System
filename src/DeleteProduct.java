import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class DeleteProduct extends JFrame implements ActionListener {

    JTextField t1;
    JButton b1, b2;

    // ── Colors matching Dashboard ─────────────────────────────────────
    static final Color BG_TOP       = new Color(245, 247, 255);
    static final Color BG_BTM       = new Color(230, 235, 252);
    static final Color CARD_FILL    = new Color(255, 255, 255, 245);
    static final Color ACCENT       = new Color(255,  90,  90); // red — matches Delete Product
    static final Color TEXT_PRIMARY = new Color(25,  30,  60);
    static final Color TEXT_MUTED   = new Color(100, 115, 155);
    static final Color FIELD_BG     = new Color(245, 247, 255);
    static final Color FIELD_BORDER = new Color(200, 210, 235);
    static final Color FIELD_FOCUS  = new Color(255,  90,  90, 160);

    DeleteProduct() {
        setTitle("Delete Product");
        setSize(440, 460);
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

                RadialGradientPaint rgp = new RadialGradientPaint(
                        new Point2D.Float(getWidth()/2f, getHeight()/3f),
                        getWidth() * 0.7f, new float[]{0f, 1f},
                        new Color[]{ new Color(255, 90, 90, 18), new Color(0, 0, 0, 0) });
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
                        0, 0, new Color(255, 90, 90, 10),
                        0, getHeight()/3, new Color(0, 0, 0, 0));
                g2.setPaint(tint);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

                // Border
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        card.setPreferredSize(new Dimension(375, 370));
        card.setOpaque(false);

        // ── Header Icon ───────────────────────────────────────────────
        try {
            ImageIcon deleteIcon = new ImageIcon("images/delete_1.png");
            if (deleteIcon.getIconWidth() > 0) {
                Image scaled = deleteIcon.getImage().getScaledInstance(52, 52, Image.SCALE_SMOOTH);
                JLabel icon = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
                icon.setBounds(0, 22, 375, 56);
                card.add(icon);
            } else {
                addFallbackIcon(card);
            }
        } catch (Exception ex) {
            addFallbackIcon(card);
        }

        // ── Title ─────────────────────────────────────────────────────
        JLabel title = new JLabel("Delete Product", SwingConstants.CENTER);
        title.setForeground(TEXT_PRIMARY);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(0, 82, 375, 26);
        card.add(title);

        JLabel subtitle = new JLabel("Enter product ID to permanently remove it", SwingConstants.CENTER);
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

        // ── Warning Box ───────────────────────────────────────────────
        JPanel warningBox = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 90, 90, 15));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(new Color(255, 90, 90, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            }
        };
        warningBox.setBounds(30, 142, 315, 40);
        warningBox.setOpaque(false);

        JLabel warningText = new JLabel("This action cannot be undone!");
        warningText.setForeground(new Color(200, 60, 60));
        warningText.setFont(new Font("Segoe UI", Font.BOLD, 11));
        warningText.setBounds(10, 0, 295, 40);
        warningBox.add(warningText);
        card.add(warningBox);

        // ── Field ─────────────────────────────────────────────────────
        t1 = createField(card, "PRODUCT ID", "Enter product ID to delete", 195);
        t1.addActionListener(this);

        // ── Delete Button ─────────────────────────────────────────────
        b1 = new JButton("Delete Product") {
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
                        g2.setColor(new Color(255, 90, 90, (int)(glow * 15)));
                        g2.fillRoundRect(-i, -i/2, getWidth()+i*2, getHeight()+i, 14+i, 14+i);
                    }
                }

                Color c1 = new Color(
                        (int)(240 + glow * 15),
                        (int)(70  + glow * 10),
                        (int)(70  + glow * 10));
                Color c2 = new Color(
                        (int)(210 + glow * 10),
                        (int)(50  + glow * 10),
                        (int)(50  + glow * 10));
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(1, 1, getWidth()-2, getHeight()/2-2, 10, 10);

                g2.setColor(new Color(180, 40, 40, 150));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                // Shadow
                g2.setColor(new Color(100, 0, 0, 80));
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText()))/2 + 1,
                        (getHeight() - fm.getHeight())/2 + fm.getAscent() + 1);
                // Text
                g2.setColor(Color.WHITE);
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText()))/2,
                        (getHeight() - fm.getHeight())/2 + fm.getAscent());
            }
        };
        b1.setBounds(35, 305, 145, 44);
        b1.setOpaque(false);
        b1.setContentAreaFilled(false);
        b1.setBorderPainted(false);
        b1.setFocusPainted(false);
        b1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b1.addActionListener(this);
        card.add(b1);

        // ── Cancel Button ─────────────────────────────────────────────
        b2 = new JButton("Cancel") {
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
        b2.setBounds(195, 305, 145, 44);
        b2.setOpaque(false);
        b2.setContentAreaFilled(false);
        b2.setBorderPainted(false);
        b2.setFocusPainted(false);
        b2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b2.addActionListener(ev -> dispose());
        card.add(b2);

        bg.add(card);
        add(bg);
        setVisible(true);
    }

    // ── Fallback Icon ─────────────────────────────────────────────────
    void addFallbackIcon(JPanel card) {
        JLabel icon = new JLabel("✕", SwingConstants.CENTER) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth()/2, cy = getHeight()/2;
                g2.setColor(new Color(255, 90, 90, 25));
                g2.fillOval(cx-26, cy-26, 52, 52);
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx-26, cy-26, 52, 52);
                super.paintComponent(g);
            }
        };
        icon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        icon.setForeground(ACCENT);
        icon.setBounds(0, 22, 375, 56);
        card.add(icon);
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
        String idStr = t1.getText().trim();

        if (idStr.isEmpty() || idStr.equals("Enter product ID to delete")) {
            showError("Please enter a product ID."); return;
        }

        try {
            int id = Integer.parseInt(idStr);
            if (id <= 0) { showError("Product ID must be greater than 0."); return; }

            // Confirm before deleting
            int confirm = JOptionPane.showOptionDialog(
                    this,
                    "Are you sure you want to delete product ID " + id + "?\nThis action cannot be undone!",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new Object[]{ "Yes, Delete", "Cancel" },
                    "Cancel"
            );

            if (confirm != 0) return;

            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM products WHERE id=?"
            );
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                showSuccess("Product ID " + id + " deleted successfully!");
                t1.setText("Enter product ID to delete");
                t1.setForeground(TEXT_MUTED);
            } else {
                showError("No product found with ID " + id + ".");
            }

        } catch (NumberFormatException ex) {
            showError("Product ID must be a valid number.");
        } catch (Exception ex) {
            showError("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ── Dialogs ───────────────────────────────────────────────────────
    void showError(String msg) {
        FancyDialog.showError(this, msg,
                "Delete Failed",
                "Please check the product ID and try again.");
    }
    void showSuccess(String msg) {
        FancyDialog.showSuccess(this, msg,
                "Product Deleted",
                "Product has been permanently removed from inventory.");
    }

    // ── Rounded Border ────────────────────────────────────────────────
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