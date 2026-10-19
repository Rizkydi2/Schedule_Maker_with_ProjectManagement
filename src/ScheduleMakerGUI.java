package src;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

public class ScheduleMakerGUI extends JFrame {

    // Palette Warna Desain Modern (Tailwind CSS-Inspired)
    private final Color COLOR_APP_BG   = new Color(241, 245, 249); 
    private final Color COLOR_CARD_BG  = new Color(255, 255, 255); 
    private final Color COLOR_NAVY     = new Color(15, 23, 42);     
    private final Color COLOR_TEXT_MAIN = new Color(51, 65, 85);    
    private final Color COLOR_TEXT_MUTED = new Color(148, 163, 184); 
    private final Color COLOR_BORDER    = new Color(226, 232, 240); 

    // Tipografi Premium
    private final Font FONT_HEADER_TITLE    = new Font("Segoe UI", Font.BOLD, 22);
    private final Font FONT_HEADER_SUB      = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_INTERFACE_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_INTERFACE_INPUT = new Font("Segoe UI", Font.PLAIN, 13);

    // Komponen Form Input
    private final JTextField txtNama, txtHari, txtMulai, txtSelesai, txtPrioritas, txtDeskripsi;
    private final JComboBox<String> cbJenis;
    private final JTable tabelJadwal;
    private final DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter; 

    public ScheduleMakerGUI() {
        // Konfigurasi Frame Utama Aplikasi
        setTitle("Schedule Maker - Ultra Premium Dashboard");
        setSize(1250, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_APP_BG);
        setLayout(new BorderLayout());

        // 1. BANNER UTAMA
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_NAVY);
        headerPanel.setBorder(new EmptyBorder(22, 35, 22, 35));

        JLabel lblTitle = new JLabel("📅  SCHEDULE MAKER SYSTEM");
        lblTitle.setFont(FONT_HEADER_TITLE);
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSub = new JLabel("Workspace Manajemen Aktivitas, Jadwal Kuliah & Agenda Harian");
        lblSub.setFont(FONT_HEADER_SUB);
        lblSub.setForeground(COLOR_TEXT_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Workspace Area
        JPanel workspacePanel = new JPanel(new BorderLayout(25, 0));
        workspacePanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        workspacePanel.setBackground(COLOR_APP_BG);

        // 2. SIDEBAR PANEL (FORM INPUT)
        JPanel sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16); 
                g2.dispose();
            }
        };
        sidebarPanel.setOpaque(false);
        sidebarPanel.setPreferredSize(new Dimension(350, 0));
        sidebarPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        sidebarPanel.setLayout(new GridLayout(15, 1, 0, 4));

        txtNama = createRoundedTextField();
        txtHari = createRoundedTextField();
        txtMulai = createRoundedTextField();
        txtSelesai = createRoundedTextField();
        txtDeskripsi = createRoundedTextField();
        
        // Validasi txtPrioritas: Hanya angka 1-5 dan maksimal 1 digit
        txtPrioritas = createRoundedTextField();
        txtPrioritas.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                String text = txtPrioritas.getText();

                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE && c != java.awt.event.KeyEvent.VK_DELETE) {
                    evt.consume();
                    return;
                }

                if (Character.isDigit(c)) {
                    if (c < '1' || c > '5' || text.length() >= 1) {
                        evt.consume(); 
                    }
                }
            }
        });
        
        String[] jenisPilihan = {"KULIAH", "KEGIATAN HARIAN"};
        cbJenis = new JComboBox<>(jenisPilihan);
        cbJenis.setFont(FONT_INTERFACE_INPUT);
        cbJenis.setBackground(Color.WHITE);
        cbJenis.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));

        sidebarPanel.add(createLabel("Nama Kegiatan"));
        sidebarPanel.add(txtNama);
        sidebarPanel.add(createLabel("Jenis Aktivitas"));
        sidebarPanel.add(cbJenis);
        sidebarPanel.add(createLabel("Hari Pelaksanaan"));
        sidebarPanel.add(txtHari);
        sidebarPanel.add(createLabel("Waktu Mulai"));
        sidebarPanel.add(txtMulai);
        sidebarPanel.add(createLabel("Waktu Selesai"));
        sidebarPanel.add(txtSelesai);
        sidebarPanel.add(createLabel("Skala Prioritas (1 - 5)"));
        sidebarPanel.add(txtPrioritas);
        sidebarPanel.add(createLabel("Keterangan / Deskripsi"));
        sidebarPanel.add(txtDeskripsi);

        workspacePanel.add(sidebarPanel, BorderLayout.WEST);

        // 3. MAIN DATA GRID AREA (TABEL)
        String[] kolom = {"Nama", "Jenis", "Hari", "Mulai", "Selesai", "Prioritas", "Deskripsi"};
        tableModel = new DefaultTableModel(kolom, 0);
        tabelJadwal = new JTable(tableModel);
        tabelJadwal.setFont(FONT_INTERFACE_INPUT);
        tabelJadwal.setRowHeight(38); 
        tabelJadwal.setGridColor(new Color(241, 245, 249));
        tabelJadwal.setSelectionBackground(new Color(14, 165, 233, 35)); 
        tabelJadwal.setSelectionForeground(COLOR_TEXT_MAIN);
        tabelJadwal.setShowVerticalLines(false); 

        // KONFIGURASI AUTOMATIC SORTING BERDASARKAN PRIORITAS
        sorter = new TableRowSorter<>(tableModel);
        tabelJadwal.setRowSorter(sorter);
        
        sorter.setComparator(5, (o1, o2) -> {
            Integer i1 = Integer.parseInt(o1.toString());
            Integer i2 = Integer.parseInt(o2.toString());
            return i1.compareTo(i2);
        });

        tabelJadwal.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                setBorder(noFocusBorder); 
                return c;
            }
        });

        JTableHeader tableHeader = tabelJadwal.getTableHeader();
        tableHeader.setFont(FONT_INTERFACE_LABEL);
        tableHeader.setBackground(new Color(241, 245, 249));
        tableHeader.setForeground(COLOR_NAVY);
        tableHeader.setPreferredSize(new Dimension(0, 42));
        tableHeader.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tabelJadwal);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        JPanel mainAreaPanel = new JPanel(new BorderLayout(0, 25));
        mainAreaPanel.setBackground(COLOR_APP_BG);
        mainAreaPanel.add(scrollPane, BorderLayout.CENTER);

        tabelJadwal.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = tabelJadwal.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = tabelJadwal.convertRowIndexToModel(viewRow);
                    txtNama.setText(tableModel.getValueAt(modelRow, 0).toString());
                    cbJenis.setSelectedItem(tableModel.getValueAt(modelRow, 1).toString());
                    txtHari.setText(tableModel.getValueAt(modelRow, 2).toString());
                    txtMulai.setText(tableModel.getValueAt(modelRow, 3).toString());
                    txtSelesai.setText(tableModel.getValueAt(modelRow, 4).toString());
                    txtPrioritas.setText(tableModel.getValueAt(modelRow, 5).toString());
                    txtDeskripsi.setText(tableModel.getValueAt(modelRow, 6).toString());
                }
            }
        });

        // 4. ACTION BAR BUTTONS
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionButtonPanel.setBackground(COLOR_APP_BG);

        JButton btnTambah = new CustomRoundedButton("➕  Tambah Jadwal", new Color(14, 165, 233)); 
        JButton btnEdit   = new CustomRoundedButton("✏️  Edit Jadwal", new Color(245, 158, 11));    
        JButton btnHapus  = new CustomRoundedButton("🗑️  Hapus Jadwal", new Color(239, 68, 68));   
        JButton btnReset  = new CustomRoundedButton("🔄  Reset", new Color(100, 116, 139));        
        JButton btnKeluar = new CustomRoundedButton("🚪  Keluar", COLOR_NAVY);

        // LOGIKA TAMBAH JADWAL
        btnTambah.addActionListener(e -> {
            if (txtNama.getText().trim().isEmpty() || txtHari.getText().trim().isEmpty() || txtPrioritas.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama Kegiatan, Hari, dan Skala Prioritas wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                tableModel.addRow(new Object[]{
                    txtNama.getText(), cbJenis.getSelectedItem().toString(), txtHari.getText(),
                    txtMulai.getText(), txtSelesai.getText(), txtPrioritas.getText(), txtDeskripsi.getText()
                });
                triggerSort(); 
                clearFields();
            }
        });

        // LOGIKA EDIT JADWAL
        btnEdit.addActionListener(e -> {
            int viewRow = tabelJadwal.getSelectedRow();
            if (viewRow >= 0) {
                if (txtPrioritas.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Skala Prioritas tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int modelRow = tabelJadwal.convertRowIndexToModel(viewRow);
                tableModel.setValueAt(txtNama.getText(), modelRow, 0);
                tableModel.setValueAt(cbJenis.getSelectedItem().toString(), modelRow, 1);
                tableModel.setValueAt(txtHari.getText(), modelRow, 2);
                tableModel.setValueAt(txtMulai.getText(), modelRow, 3);
                tableModel.setValueAt(txtSelesai.getText(), modelRow, 4);
                tableModel.setValueAt(txtPrioritas.getText(), modelRow, 5);
                tableModel.setValueAt(txtDeskripsi.getText(), modelRow, 6);
                triggerSort(); 
                JOptionPane.showMessageDialog(this, "Jadwal berhasil diperbarui!", "Sukses Dashboard", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris pada tabel terlebih dahulu untuk diedit!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // LOGIKA HAPUS JADWAL
        btnHapus.addActionListener(e -> {
            int viewRow = tabelJadwal.getSelectedRow();
            if (viewRow >= 0) {
                int modelRow = tabelJadwal.convertRowIndexToModel(viewRow);
                tableModel.removeRow(modelRow);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris tabel yang ingin dihapus terlebih dahulu!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // LOGIKA TOMBOL RESET (MENGHAPUS SEMUA INPUT DAN SEMUA BARIS TABEL)
        btnReset.addActionListener(e -> {
            // 1. Kosongkan seluruh baris di tabelJadwal
            tableModel.setRowCount(0);
            // 2. Kosongkan semua komponen text field di form kiri
            clearFields();
            JOptionPane.showMessageDialog(this, "Semua jadwal berhasil dihapus!", "Reset Sukses", JOptionPane.INFORMATION_MESSAGE);
        });

        // LOGIKA TOMBOL KELUAR
        btnKeluar.addActionListener(e -> System.exit(0));

        actionButtonPanel.add(btnTambah);
        actionButtonPanel.add(btnEdit);
        actionButtonPanel.add(btnHapus);
        actionButtonPanel.add(btnReset);
        actionButtonPanel.add(btnKeluar);

        mainAreaPanel.add(actionButtonPanel, BorderLayout.SOUTH);
        workspacePanel.add(mainAreaPanel, BorderLayout.CENTER);
        add(workspacePanel, BorderLayout.CENTER);
    }

    private void triggerSort() {
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(5, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }

    private void clearFields() {
        txtNama.setText(""); 
        txtHari.setText(""); 
        txtMulai.setText("");
        txtSelesai.setText(""); 
        txtPrioritas.setText(""); 
        txtDeskripsi.setText("");
        cbJenis.setSelectedIndex(0);
        
        if (tabelJadwal != null) {
            tabelJadwal.clearSelection();
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_INTERFACE_LABEL);
        label.setForeground(COLOR_TEXT_MAIN);
        return label;
    }

    private JTextField createRoundedTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        field.setFont(FONT_INTERFACE_INPUT);
        field.setOpaque(false); 
        field.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12)); 
        return field;
    }

    static class CustomRoundedButton extends JButton {
        private final Color baseColor;

        public CustomRoundedButton(String label, Color baseColor) {
            super(label);
            this.baseColor = baseColor;
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { setBackground(baseColor.darker()); }
                @Override
                public void mouseExited(MouseEvent e) { setBackground(baseColor); }
            });
            setBackground(baseColor);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14); 
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}