import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class SalesChart extends JFrame {

    static final Color BG_TOP       = new Color(245, 247, 255);
    static final Color BG_BTM       = new Color(230, 235, 252);
    static final Color CARD_FILL    = new Color(255, 255, 255, 245);
    static final Color ACCENT       = new Color(255, 150,  80); 
    static final Color TEXT_PRIMARY = new Color(25,  30,  60);
    static final Color TEXT_MUTED   = new Color(100, 115, 155);

    static final Color[] BAR_COLORS = {
            new Color(82,  153, 255),
            new Color(82,  200, 140),
            new Color(255, 150,  80),
            new Color(255,  90,  90),
            new Color(180, 130, 255),
            new Color(60,  200, 220),
            new Color(255, 190,  60),
            new Color(150, 200, 100),
    };

    public SalesChart() {
        setTitle("Sales Chart");
        setSize(780, 580);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel bg = new JPanel(new BorderLayout(0, 0)) {
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
                        new Color[]{ new Color(255, 150, 80, 15), new Color(0, 0, 0, 0) });
                g2.setPaint(rgp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bg.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topCard = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                for (int i = 4; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 4));
                    g2.fillRoundRect(-i/2, i, getWidth()+i, getHeight()+i/2, 16+i, 16+i);
                }

                g2.setColor(CARD_FILL);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);

                GradientPaint strip = new GradientPaint(
                        0, 0,
                        new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 230),
                        getWidth(), 0,
                        new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 50));
                g2.setPaint(strip);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 14, 14));
                g2.fillRect(0, 0, getWidth(), 5);
                g2.setClip(null);

                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
            }
        };
        topCard.setPreferredSize(new Dimension(0, 70));
        topCard.setOpaque(false);

        try {
            ImageIcon chartIcon = new ImageIcon("images/chart_1.png");
            if (chartIcon.getIconWidth() > 0) {
                Image scaled = chartIcon.getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
                JLabel iconLbl = new JLabel(new ImageIcon(scaled));
                iconLbl.setBounds(18, 17, 36, 36);
                topCard.add(iconLbl);
            }
        } catch (Exception ignored) {}

        JLabel title = new JLabel("Sales Chart");
        title.setForeground(TEXT_PRIMARY);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(64, 12, 300, 26);
        topCard.add(title);

        JLabel subtitle = new JLabel("Revenue breakdown by product");
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setBounds(64, 38, 300, 16);
        topCard.add(subtitle);

        JButton refreshBtn = new JButton("Refresh") {
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
                        ? new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 220)
                        : new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(1, 1, getWidth()-2, getHeight()/2-2, 8, 8);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            }
        };
        refreshBtn.setBounds(0, 0, 100, 34);
        refreshBtn.setOpaque(false);
        refreshBtn.setContentAreaFilled(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        topCard.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                refreshBtn.setBounds(topCard.getWidth() - 130, 18, 110, 34);
            }
        });
        topCard.add(refreshBtn);
        bg.add(topCard, BorderLayout.NORTH);

        JPanel chartCard = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                for (int i = 5; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 4));
                    g2.fillRoundRect(-i/2, i, getWidth()+i, getHeight()+i/2, 16+i, 16+i);
                }
                g2.setColor(CARD_FILL);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 50));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
            }
        };
        chartCard.setOpaque(false);
        chartCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        DefaultCategoryDataset dataset = loadData();
        JFreeChart chart = buildChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(new Color(0, 0, 0, 0));
        chartCard.add(chartPanel, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> {
            chartCard.removeAll();
            DefaultCategoryDataset newData = loadData();
            JFreeChart newChart = buildChart(newData);
            ChartPanel newPanel = new ChartPanel(newChart);
            newPanel.setOpaque(false);
            chartCard.add(newPanel, BorderLayout.CENTER);
            chartCard.revalidate();
            chartCard.repaint();
        });

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        centerWrapper.add(chartCard, BorderLayout.CENTER);
        bg.add(centerWrapper, BorderLayout.CENTER);

        add(bg);
        setVisible(true);
    }

    DefaultCategoryDataset loadData() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT product_name, SUM(total) as total FROM sales GROUP BY product_name"
            );
            while (rs.next()) {
                dataset.setValue(
                        rs.getDouble("total"),
                        "Revenue (₹)",
                        rs.getString("product_name")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataset;
    }

    JFreeChart buildChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                null,           // title (we show it in card header)
                "Product",
                "Revenue (₹)",
                dataset,
                PlotOrientation.VERTICAL,
                false,          // legend
                true,           // tooltips
                false           // urls
        );

        chart.setBackgroundPaint(new Color(0, 0, 0, 0));
        chart.setBorderVisible(false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(248, 250, 255));
        plot.setOutlineVisible(false);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinePaint(new Color(220, 225, 245));
        plot.setRangeGridlineStroke(new BasicStroke(1f));

        BarRenderer renderer = new BarRenderer() {
            public Paint getItemPaint(int row, int col) {
                return BAR_COLORS[col % BAR_COLORS.length];
            }
        };
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.08);
        renderer.setItemMargin(0.15);
s
        renderer.setItemLabelsVisible(true);
        renderer.setItemLabelGenerator(
                new org.jfree.chart.labels.StandardCategoryItemLabelGenerator(
                        "₹{2}", new java.text.DecimalFormat("#,##0.00")));
        renderer.setItemLabelFont(new Font("Segoe UI", Font.BOLD, 11));
        renderer.setItemLabelPaint(TEXT_PRIMARY);
        renderer.setPositiveItemLabelPosition(
                new org.jfree.chart.labels.ItemLabelPosition(
                        org.jfree.chart.labels.ItemLabelAnchor.OUTSIDE12,
                        org.jfree.ui.TextAnchor.BOTTOM_CENTER));

        plot.setRenderer(renderer);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        domainAxis.setTickLabelPaint(TEXT_MUTED);
        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        domainAxis.setLabelPaint(TEXT_PRIMARY);
        domainAxis.setAxisLinePaint(new Color(210, 215, 235));
        domainAxis.setTickMarkPaint(new Color(210, 215, 235));
        domainAxis.setCategoryMargin(0.3);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        rangeAxis.setTickLabelPaint(TEXT_MUTED);
        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        rangeAxis.setLabelPaint(TEXT_PRIMARY);
        rangeAxis.setAxisLinePaint(new Color(210, 215, 235));
        rangeAxis.setTickMarkPaint(new Color(210, 215, 235));
        rangeAxis.setNumberFormatOverride(new java.text.DecimalFormat("₹#,##0"));

        return chart;
    }
}
