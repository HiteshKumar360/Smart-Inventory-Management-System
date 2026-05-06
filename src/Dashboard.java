import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class Dashboard extends JFrame implements ActionListener {

    JButton b1, b2, b3, b4, b5, b6, b7, b8;

    static final Color SIDEBAR_BG      = new Color(10,  13,  28, 255);
    static final Color SIDEBAR_TEXT    = new Color(230, 235, 255);
    static final Color SIDEBAR_MUTED   = new Color(160, 175, 210);
    static final Color SIDEBAR_ACCENT  = new Color(82,  153, 255);

    static final Color MAIN_BG_TOP     = new Color(245, 247, 255);
    static final Color MAIN_BG_BTM     = new Color(230, 235, 252);
    static final Color CARD_FILL       = new Color(255, 255, 255, 245);
    static final Color CARD_BORDER     = new Color(0,   0,   0,   20);
    static final Color TEXT_PRIMARY    = new Color(25,  30,  60);
    static final Color TEXT_MUTED      = new Color(100, 115, 155);

    static final Object[][] MENU_ITEMS = {
            { "Add Product",    "images/add.png",    new Color(82,  200, 140), "Add new inventory item", "images/add_1.png"    },
            { "View Products",  "images/view.png",   new Color(82,  153, 255), "Browse all products",    "images/view_1.png"   },
            { "Update Stock",   "images/update.png", new Color(255, 190,  60), "Modify stock quantity",  "images/update_1.png" },
            { "Delete Product", "images/delete.png", new Color(255,  90,  90), "Remove a product",       "images/delete_1.png" },
            { "Search Product", "images/search.png", new Color(180, 130, 255), "Find product by name",   "images/search_1.png" },
            { "Sales Entry",    "images/sales.png",  new Color(60,  200, 220), "Record a new sale",      "images/sales_1.png"  },
            { "Sales Chart",    "images/chart.png",  new Color(255, 150,  80), "View sales analytics",   "images/chart_1.png"  },
            { "Logout",         "images/logout.png", new Color(180, 185, 210), "Exit current session",   "images/logout_1.png" },
    };

    Dashboard() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        setTitle("Smart Inventory — Dashboard");
        setSize(960, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 580));

        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(true);
        root.setBackground(new Color(245, 247, 255));

        SidebarPanel sidebar = new SidebarPanel();
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setLayout(new BorderLayout());

        JPanel sideTop = new JPanel(null);
        sideTop.setPreferredSize(new Dimension(230, 115));
        sideTop.setOpaque(false);

        ImageIcon rawIcon = new ImageIcon("images/logo.png");
        Image scaledImg = rawIcon.getImage().getScaledInstance(46, 46, Image.SCALE_SMOOTH);
        JLabel logoLbl = new JLabel(new ImageIcon(scaledImg), SwingConstants.CENTER);
        logoLbl.setBounds(16, 24, 46, 46);
        sideTop.add(logoLbl);

        JLabel appName = new JLabel("Smart Inventory");
        appName.setForeground(SIDEBAR_TEXT);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        appName.setBounds(70, 26, 145, 20);
        sideTop.add(appName);

        JLabel appSub = new JLabel("Management System");
        appSub.setForeground(SIDEBAR_MUTED);
        appSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        appSub.setBounds(70, 47, 145, 16);
        sideTop.add(appSub);

        JPanel sepLine = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 0, 0, 0),
                        getWidth() / 2, 0, SIDEBAR_ACCENT, true);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sepLine.setBounds(16, 86, 198, 2);
        sepLine.setOpaque(false);
        sideTop.add(sepLine);
        sidebar.add(sideTop, BorderLayout.NORTH);

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setOpaque(false);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton[] navBtns = new JButton[8];
        for (int i = 0; i < MENU_ITEMS.length; i++) {
            String label   = (String) MENU_ITEMS[i][0];
            String imgPath = (String) MENU_ITEMS[i][1]; 
            Color  color   = (Color)  MENU_ITEMS[i][2];
            JButton btn = createNavButton(label, imgPath, color);
            navBtns[i] = btn;
            navPanel.add(btn);
            navPanel.add(Box.createVerticalStrut(2));
        }

        b1 = navBtns[0]; b2 = navBtns[1]; b3 = navBtns[2]; b4 = navBtns[3];
        b5 = navBtns[4]; b6 = navBtns[5]; b7 = navBtns[6]; b8 = navBtns[7];
        sidebar.add(navPanel, BorderLayout.CENTER);

        JLabel version = new JLabel("v1.0  ·  © 2026", SwingConstants.CENTER);
        version.setForeground(new Color(60, 75, 120));
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        sidebar.add(version, BorderLayout.SOUTH);

        MainPanel main = new MainPanel();
        main.setLayout(new BorderLayout());

        JPanel topBar = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255, 255, 255, 210));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
            }
        };
        topBar.setPreferredSize(new Dimension(0, 68));
        topBar.setOpaque(false);

        JLabel pageTitle = new JLabel("Dashboard");
        pageTitle.setForeground(TEXT_PRIMARY);
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pageTitle.setBounds(28, 12, 300, 32);
        topBar.add(pageTitle);

        final JLabel greeting = new JLabel("Welcome back, Admin  ·  " + getDateTime());
        greeting.setForeground(TEXT_MUTED);
        greeting.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        greeting.setBounds(28, 44, 500, 16);
        topBar.add(greeting);
        main.add(topBar, BorderLayout.NORTH);

        new Timer(1000, e -> greeting.setText(
                "Welcome back, Admin  ·  " + getDateTime()
        )).start();

        JPanel centerStack = new JPanel(new BorderLayout());
        centerStack.setOpaque(false);
        centerStack.add(createStatsBar(), BorderLayout.NORTH);

        JPanel cardsArea = new JPanel(new GridLayout(2, 4, 16, 16));
        cardsArea.setOpaque(false);
        cardsArea.setBorder(BorderFactory.createEmptyBorder(16, 28, 22, 28));

        for (Object[] item : MENU_ITEMS) {
            cardsArea.add(createCard(
                    (String) item[0],
                    (String) item[4], 
                    (Color)  item[2]
            ));
        }
        centerStack.add(cardsArea, BorderLayout.CENTER);
        main.add(centerStack, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0)) {
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 200));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(0, 0, 0, 12));
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        statusBar.setPreferredSize(new Dimension(0, 32));
        statusBar.setOpaque(false);
        JLabel statusDot = new JLabel("●  Connected to database");
        statusDot.setForeground(new Color(60, 180, 110));
        statusDot.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.add(statusDot);
        main.add(statusBar, BorderLayout.SOUTH);

        root.add(sidebar, BorderLayout.WEST);
        root.add(main,    BorderLayout.CENTER);
        add(root);
        setVisible(true);
        checkLowStock();
    }

    JPanel createStatsBar() {
        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setOpaque(false);
        stats.setBorder(BorderFactory.createEmptyBorder(22, 28, 6, 28));
        stats.add(createStatCard("Total Products", getTotalProducts(), new Color(82,  153, 255)));
        stats.add(createStatCard("Low Stock",      getLowStock(),      new Color(255,  90,  90)));
        stats.add(createStatCard("Total Sales",    getTotalSales(),    new Color(82,  200, 140)));
        stats.add(createStatCard("Categories",     getCategories(),    new Color(180, 130, 255)));
        return stats;
    }

    JPanel createStatCard(String label, String value, Color accent) {
        JPanel card = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                for (int i = 5; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 6));
                    g2.fillRoundRect(-i/2, i, getWidth()+i, getHeight()+i/2, 16+i, 16+i);
                }

                g2.setColor(CARD_FILL);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);

                g2.setColor(accent);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 14, 14));
                g2.fillRect(0, 0, 4, getHeight());
                g2.setClip(null);

                GradientPaint strip = new GradientPaint(
                        0, 0,
                        new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 180),
                        getWidth(), 0,
                        new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
                g2.setPaint(strip);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 14, 14));
                g2.fillRect(0, getHeight()-3, getWidth(), 3);
                g2.setClip(null);

                GradientPaint tint = new GradientPaint(
                        0, 0,
                        new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 18),
                        getWidth(), getHeight(), new Color(0, 0, 0, 0));
                g2.setPaint(tint);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);

                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 50));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
            }
        };
        card.setPreferredSize(new Dimension(0, 84));
        card.setOpaque(false);

        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setForeground(TEXT_PRIMARY);
        val.setFont(new Font("Segoe UI", Font.BOLD, 30));
        val.setBounds(4, 10, 300, 38);
        card.add(val);

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setBounds(4, 50, 300, 16);
        card.add(lbl);

        return card;
    }

    JButton createNavButton(String label, String imgPath, Color accent) {
        Image navImg = loadImage(imgPath, 22);

        JButton btn = new JButton() {
            boolean hovered = false;
            float glowAlpha = 0f;
            final Timer anim = new Timer(16, null);
            {
                anim.addActionListener(e -> {
                    glowAlpha += hovered ? 0.08f : -0.08f;
                    glowAlpha = Math.max(0f, Math.min(1f, glowAlpha));
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

                if (glowAlpha > 0) {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(),
                            accent.getBlue(), (int)(glowAlpha * 25)));
                    g2.fillRoundRect(10, 2, getWidth()-20, getHeight()-4, 10, 10);
        
                    g2.setColor(accent);
                    g2.fillRoundRect(10, 6, 3, getHeight()-12, 3, 3);
                }

                g2.setColor(new Color(255, 255, 255, (int)(30 + glowAlpha * 40)));
                g2.fillOval(18, (getHeight()-28)/2, 28, 28);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(),
                        accent.getBlue(), (int)(100 + glowAlpha * 100)));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawOval(18, (getHeight()-28)/2, 28, 28);

                if (navImg != null) {
                    g2.drawImage(navImg, 21, (getHeight()-22)/2, null);
                }

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.setColor(glowAlpha > 0.3f ? SIDEBAR_TEXT : SIDEBAR_MUTED);
                g2.drawString(label, 56, getHeight()/2 + 5);
            }
        };
        btn.setPreferredSize(new Dimension(210, 42));
        btn.setMaximumSize(new Dimension(230, 42));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(this);
        return btn;
    }

    JPanel createCard(String label, String imgPath, Color accent) {
        Image cardImg = loadImage(imgPath, 40);

        JPanel card = new JPanel(null) {
            boolean hovered = false;
            float scale = 1f;
            final Timer anim = new Timer(16, null);
            {
                anim.addActionListener(e -> {
                    scale += hovered ? 0.015f : -0.015f;
                    scale = Math.max(1f, Math.min(1.04f, scale));
                    repaint();
                });
                anim.start();
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true; }
                    public void mouseExited (MouseEvent e) { hovered = false; }
                    public void mouseClicked(MouseEvent e) {
                        for (int i = 0; i < MENU_ITEMS.length; i++) {
                            if (MENU_ITEMS[i][0].equals(label)) {
                                new JButton[]{ b1,b2,b3,b4,b5,b6,b7,b8 }[i].doClick();
                                break;
                            }
                        }
                    }
                });
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int px = (int)((w - w*scale)/2), py = (int)((h - h*scale)/2);
                int sw = (int)(w*scale),          sh = (int)(h*scale);

                if (hovered) {
                    for (int i = 10; i > 0; i--) {
                        g2.setColor(new Color(accent.getRed(), accent.getGreen(),
                                accent.getBlue(), 4));
                        g2.fillRoundRect(px-i, py+i/2, sw+i*2, sh+i, 20+i, 20+i);
                    }
                } else {
                    for (int i = 5; i > 0; i--) {
                        g2.setColor(new Color(0, 0, 0, 5));
                        g2.fillRoundRect(px-i/2, py+i/2, sw+i, sh+i/2, 18, 18);
                    }
                }

                g2.setColor(CARD_FILL);
                g2.fillRoundRect(px, py, sw-1, sh-1, 16, 16);

                GradientPaint strip = new GradientPaint(
                        px, py,
                        new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 230),
                        px+sw, py,
                        new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 50));
                g2.setPaint(strip);
                g2.setClip(new RoundRectangle2D.Float(px, py, sw-1, sh-1, 16, 16));
                g2.fillRect(px, py, sw, 5);
                g2.setClip(null);

                if (hovered) {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(),
                            accent.getBlue(), 10));
                    g2.fillRoundRect(px, py, sw-1, sh-1, 16, 16);
                }

                g2.setColor(hovered
                        ? new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80)
                        : CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(px, py, sw-1, sh-1, 16, 16);

                int cx = px + sw/2, cy = py + sh/2 - 22;
                g2.setColor(new Color(accent.getRed(), accent.getGreen(),
                        accent.getBlue(), 20));
                g2.fillOval(cx-34, cy-34, 68, 68);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(),
                        accent.getBlue(), hovered ? 160 : 100));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx-34, cy-34, 68, 68);

                if (cardImg != null) {
                    g2.drawImage(cardImg, cx-20, cy-20, null);
                }

                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(TEXT_PRIMARY);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label,
                        px + (sw - fm.stringWidth(label))/2,
                        py + sh - 26);

                String subtitle = "";
                for (Object[] item : MENU_ITEMS) {
                    if (item[0].equals(label)) {
                        subtitle = (String) item[3];
                        break;
                    }
                }
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(TEXT_MUTED);
                FontMetrics fm2 = g2.getFontMetrics();
                g2.drawString(subtitle,
                        px + (sw - fm2.stringWidth(subtitle))/2,
                        py + sh - 12);
            }
        };
        card.setOpaque(false);
        return card;
    }

    Image loadImage(String path, int size) {
        try {
            ImageIcon raw = new ImageIcon(path);
            if (raw.getIconWidth() <= 0) return null;
            return raw.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    String getTotalProducts() {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                    .executeQuery("SELECT COUNT(*) FROM products");
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (Exception e) { e.printStackTrace(); }
        return "—";
    }
    String getLowStock() {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                    .executeQuery("SELECT COUNT(*) FROM products WHERE quantity < 5");
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (Exception e) { e.printStackTrace(); }
        return "—";
    }
    String getTotalSales() {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                    .executeQuery("SELECT COUNT(*) FROM sales");
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (Exception e) { e.printStackTrace(); }
        return "—";
    }
    String getCategories() {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                    .executeQuery("SELECT COUNT(DISTINCT category) FROM products");
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (Exception e) { e.printStackTrace(); }
        return "—";
    }
    String getDateTime() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return String.format("%02d %s %d  %02d:%02d:%02d",
                now.getDayOfMonth(),
                now.getMonth().toString().substring(0, 3),
                now.getYear(),
                now.getHour(),
                now.getMinute(),
                now.getSecond());
    }

    // LOW STOCK ALERT
    void checkLowStock() {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                    .executeQuery("SELECT name, quantity FROM products WHERE quantity < 5");
            StringBuilder msg = new StringBuilder();
            while (rs.next())
                msg.append(rs.getString("name"))
                        .append("  (Qty: ").append(rs.getInt("quantity")).append(")\n");
            if (msg.length() > 0)
                FancyDialog.showError(this,
                        msg.toString().trim(),
                        "Low Stock Alert",
                        "Please restock these items soon.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void actionPerformed(ActionEvent e) {
        if      (e.getSource() == b1) new AddProduct();
        else if (e.getSource() == b2) new ViewProducts();
        else if (e.getSource() == b3) new UpdateProduct();
        else if (e.getSource() == b4) new DeleteProduct();
        else if (e.getSource() == b5) new SearchProduct();
        else if (e.getSource() == b6) new SalesEntry();
        else if (e.getSource() == b7) new SalesChart();
        else if (e.getSource() == b8) { dispose(); new Login(); }
    }

    static class SidebarPanel extends JPanel {
        private final int[][] stars;
        private final Timer timer;
        private float phase = 0;

        SidebarPanel() {
            setOpaque(true);
            stars = new int[40][3];
            java.util.Random rng = new java.util.Random();
            for (int[] s : stars) {
                s[0] = rng.nextInt(230);
                s[1] = rng.nextInt(700);
                s[2] = rng.nextInt(2) + 1;
            }
            timer = new Timer(50, e -> { phase += 0.02f; repaint(); });
            timer.start();
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(10, 12, 20),
                    0, getHeight(), new Color(14, 18, 42));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            RadialGradientPaint rgp = new RadialGradientPaint(
                    new Point2D.Float(getWidth()/2f, getHeight()/2f),
                    getWidth() * 1.2f, new float[]{0f, 1f},
                    new Color[]{ new Color(40, 80, 160, 50), new Color(0, 0, 0, 0) });
            g2.setPaint(rgp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            for (int[] s : stars) {
                float alpha = 0.3f + 0.5f * (float)Math.abs(Math.sin(phase + s[0]*0.1f));
                g2.setColor(new Color(180, 210, 255, (int)(alpha * 255)));
                g2.fillOval(s[0], s[1], s[2], s[2]);
            }

            GradientPaint border = new GradientPaint(
                    getWidth()-8, 0, new Color(0, 0, 0, 0),
                    getWidth(), 0, new Color(82, 153, 255, 25));
            g2.setPaint(border);
            g2.fillRect(getWidth()-8, 0, 8, getHeight());

            g2.setColor(new Color(82, 153, 255, 40));
            g2.fillRect(getWidth()-1, 0, 1, getHeight());
        }
    }

    static class MainPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(
                    0, 0, MAIN_BG_TOP,
                    0, getHeight(), MAIN_BG_BTM);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            RadialGradientPaint rgp = new RadialGradientPaint(
                    new Point2D.Float(getWidth()/2f, getHeight()/3f),
                    getWidth() * 0.7f, new float[]{0f, 1f},
                    new Color[]{ new Color(200, 215, 255, 40), new Color(0, 0, 0, 0) });
            g2.setPaint(rgp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
