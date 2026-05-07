import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class ViewProducts extends JFrame {

    JTable table;
    DefaultTableModel model;
    JLabel countLbl;

    static final Color BG_TOP       = new Color(245, 247, 255);
    static final Color BG_BTM       = new Color(230, 235, 252);
    static final Color CARD_FILL    = new Color(255, 255, 255, 245);
    static final Color ACCENT       = new Color(82,  153, 255);
    static final Color TEXT_PRIMARY = new Color(25,  30,  60);
    static final Color TEXT_MUTED   = new Color(100, 115, 155);
    static final Color ROW_ALT      = new Color(245, 248, 255);
    static final Color LOW_STOCK_BG = new Color(255, 240, 240);
    static final Color LOW_STOCK_FG = new Color(200,  60,  60);

    ViewProducts() {
        setTitle("View Products");
        setSize(760, 560);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel bg = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, BG_TOP, 0, getHeight(), BG_BTM);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bg.setLayout(new BorderLayout(0, 0));
        bg.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topCard = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i = 4; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 4));
                    g2.fillRoundRect(-i/2, i, getWidth()+i,
                            getHeight()+i/2, 16+i, 16+i);
                }
                g2.setColor(CARD_FILL);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                GradientPaint strip = new GradientPaint(
                        0, 0,
                        new Color(ACCENT.getRed(), ACCENT.getGreen(),
                                ACCENT.getBlue(), 230),
                        getWidth(), 0,
                        new Color(ACCENT.getRed(), ACCENT.getGreen(),
                                ACCENT.getBlue(), 50));
                g2.setPaint(strip);
                g2.setClip(new RoundRectangle2D.Float(
                        0, 0, getWidth()-1, getHeight()-1, 14, 14));
                g2.fillRect(0, 0, getWidth(), 5);
                g2.setClip(null);
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(),
                        ACCENT.getBlue(), 50));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
            }
        };
        topCard.setPreferredSize(new Dimension(0, 72));
        topCard.setOpaque(false);

        try {
            ImageIcon ic = new ImageIcon("images/view_1.png");
            if (ic.getIconWidth() > 0) {
                Image img = ic.getImage().getScaledInstance(34, 34, Image.SCALE_SMOOTH);
                JLabel iconLbl = new JLabel(new ImageIcon(img));
                iconLbl.setBounds(16, 18, 34, 34);
                topCard.add(iconLbl);
            }
        } catch (Exception ignored) {}

        JLabel title = new JLabel("View Products");
        title.setForeground(TEXT_PRIMARY);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(60, 14, 300, 26);
        topCard.add(title);

        JLabel subtitle = new JLabel("All products in your inventory");
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setBounds(60, 40, 300, 16);
        topCard.add(subtitle);

        JPanel legendDot = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(LOW_STOCK_FG);
                g2.fillOval(0, 3, 8, 8);
            }
        };
        legendDot.setBounds(380, 32, 10, 14);
        legendDot.setOpaque(false);
        topCard.add(legendDot);

        JLabel legendLbl = new JLabel("Low stock (qty < 5)");
        legendLbl.setForeground(LOW_STOCK_FG);
        legendLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        legendLbl.setBounds(394, 30, 140, 16);
        topCard.add(legendLbl);

        JButton refreshBtn = createAccentButton("Refresh");
        refreshBtn.setBounds(560, 18, 130, 36);
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            loadData();
        });
        topCard.add(refreshBtn);

        bg.add(topCard, BorderLayout.NORTH);

        JPanel tableCard = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i = 5; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 4));
                    g2.fillRoundRect(-i/2, i, getWidth()+i,
                            getHeight()+i/2, 16+i, 16+i);
                }
                g2.setColor(CARD_FILL);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(),
                        ACCENT.getBlue(), 40));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
            }
        };
        tableCard.setOpaque(false);
        tableCard.setBorder(BorderFactory.createEmptyBorder(14, 14, 8, 14));

        model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        model.addColumn("ID");
        model.addColumn("Product Name");
        model.addColumn("Category");
        model.addColumn("Price (₹)");
        model.addColumn("Quantity");
        model.addColumn("Status");

        table = new JTable(model) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);

                Object qtyObj = model.getValueAt(row, 4);
                boolean lowStock = false;
                try {
                    lowStock = Integer.parseInt(qtyObj.toString()) < 5;
                } catch (Exception ignored) {}

                if (isRowSelected(row)) {
                    c.setBackground(new Color(ACCENT.getRed(),
                            ACCENT.getGreen(), ACCENT.getBlue(), 80));
                    c.setForeground(TEXT_PRIMARY);
                } else if (lowStock) {
                    c.setBackground(LOW_STOCK_BG);
                    c.setForeground(col == 4 ? LOW_STOCK_FG : TEXT_PRIMARY);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                    c.setForeground(TEXT_PRIMARY);
                }

                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(
                            BorderFactory.createEmptyBorder(0, 12, 0, 12));
                    if (col == 0 || col == 4) {
                        ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                    } else {
                        ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                    }

                    if (col == 5) {
                        try {
                            Object val = model.getValueAt(row, 5);
                            if (val != null && val.toString().contains("Low")) {
                                c.setForeground(LOW_STOCK_FG);
                                ((JLabel) c).setFont(new Font("Segoe UI", Font.BOLD, 12));
                            } else {
                                c.setForeground(new Color(60, 180, 110));
                                ((JLabel) c).setFont(new Font("Segoe UI", Font.BOLD, 12));
                            }
                        } catch (Exception ignored) {}
                    }

                }
                return c;
            }
        };


        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(ACCENT.getRed(),
                ACCENT.getGreen(), ACCENT.getBlue(), 80));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFocusable(false);
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 42));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel,
                    boolean foc, int row, int col) {
                return new JLabel(val == null ? "" : val.toString()) {
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        GradientPaint gp = new GradientPaint(
                                0, 0, ACCENT,
                                0, getHeight(), new Color(60, 130, 235));
                        g2.setPaint(gp);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.setColor(new Color(255, 255, 255, 25));
                        g2.fillRect(0, 0, getWidth(), getHeight()/2);

                        g2.setColor(new Color(255, 255, 255, 30));
                        g2.drawLine(getWidth()-1, 4,
                                getWidth()-1, getHeight()-4);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        g2.setColor(Color.WHITE);
                        g2.drawString(val == null ? "" : val.toString(),
                                12, getHeight()/2 +
                                        g2.getFontMetrics().getAscent()/2);
                    }
                };
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUI(
                new javax.swing.plaf.basic.BasicScrollBarUI() {
                    protected void configureScrollBarColors() {
                        thumbColor = new Color(ACCENT.getRed(),
                                ACCENT.getGreen(), ACCENT.getBlue(), 120);
                        trackColor = new Color(240, 243, 255);
                    }
                    protected JButton createDecreaseButton(int o) {
                        return zeroButton();
                    }
                    protected JButton createIncreaseButton(int o) {
                        return zeroButton();
                    }
                    private JButton zeroButton() {
                        JButton b = new JButton();
                        b.setPreferredSize(new Dimension(0, 0));
                        return b;
                    }
                });

        tableCard.add(scroll, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setOpaque(false);
        statusBar.setPreferredSize(new Dimension(0, 32));
        statusBar.setBorder(BorderFactory.createEmptyBorder(6, 4, 0, 4));

        countLbl = new JLabel("Loading...");
        countLbl.setForeground(TEXT_MUTED);
        countLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.add(countLbl, BorderLayout.WEST);

        JLabel lowLbl = new JLabel();
        lowLbl.setForeground(LOW_STOCK_FG);
        lowLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.add(lowLbl, BorderLayout.EAST);

        model.addTableModelListener(e -> {
            int total = model.getRowCount();
            int low = 0;
            for (int i = 0; i < total; i++) {
                try {
                    if (Integer.parseInt(
                            model.getValueAt(i, 4).toString()) < 5) low++;
                } catch (Exception ignored) {}
            }
            countLbl.setText("Showing " + total + " product(s)");
            lowLbl.setText(low > 0
                    ? "! " + low + " low stock item(s)   " : "");
        });

        tableCard.add(statusBar, BorderLayout.SOUTH);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        centerWrapper.add(tableCard, BorderLayout.CENTER);
        bg.add(centerWrapper, BorderLayout.CENTER);

        add(bg);
        loadData();
        setVisible(true);
    }

    void loadData() {
        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement()
                    .executeQuery("SELECT * FROM products ORDER BY id");
            while (rs.next()) {
                int qty = rs.getInt("quantity");
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        String.format("₹ %.2f", rs.getDouble("price")),
                        qty,
                        qty < 5 ? "! Low" : "OK"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            FancyDialog.showError(this,
                    "Error loading products: " + e.getMessage(),
                    "Load Failed",
                    "Please check your database connection.");
        }
    }

    JButton createAccentButton(String text) {
        JButton btn = new JButton(text) {
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
                Color c = hovered
                        ? new Color(ACCENT.getRed(), ACCENT.getGreen(),
                        ACCENT.getBlue(), 230)
                        : new Color(ACCENT.getRed(), ACCENT.getGreen(),
                        ACCENT.getBlue(), 190);
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRoundRect(1, 1, getWidth()-2, getHeight()/2-2, 8, 8);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText()))/2,
                        (getHeight() - fm.getHeight())/2 + fm.getAscent());
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
