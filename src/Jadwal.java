package src;

import java.util.ArrayList;
import java.util.List;


public class Jadwal {
    private String namaJadwal;
    private String deskripsi;
    private List<AbstractKegiatan> daftarKegiatan;

    public Jadwal(String namaJadwal, String deskripsi) {
        this.namaJadwal = namaJadwal;
        this.deskripsi = deskripsi;
        this.daftarKegiatan = new ArrayList<>();
    }

    public Jadwal(String namaJadwal) {
        this(namaJadwal, "Tidak ada deskripsi");
    }

    public String getNamaJadwal() { return namaJadwal; }
    public void setNamaJadwal(String namaJadwal) { this.namaJadwal = namaJadwal; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public List<AbstractKegiatan> getDaftarKegiatan() { return daftarKegiatan; }

    public void tambahKegiatan(AbstractKegiatan kegiatan) {
        daftarKegiatan.add(kegiatan);
        System.out.println("  ✓ Kegiatan '" + kegiatan.getNamaKegiatan() + "' berhasil ditambahkan.");
    }

    public boolean hapusKegiatan(int index) {
        if (index >= 0 && index < daftarKegiatan.size()) {
            String nama = daftarKegiatan.get(index).getNamaKegiatan();
            daftarKegiatan.remove(index);
            System.out.println("  ✓ Kegiatan '" + nama + "' berhasil dihapus.");
            return true;
        }
        System.out.println("  ✗ Index tidak valid.");
        return false;
    }

    public void tampilkanSemua() {
        if (daftarKegiatan.isEmpty()) {
            System.out.println("  (Belum ada kegiatan dalam jadwal ini)");
            return;
        }
        for (int i = 0; i < daftarKegiatan.size(); i++) {
            System.out.println("  ---- Kegiatan #" + (i + 1) + " ----");
            daftarKegiatan.get(i).tampilkanDetail(); // Polymorphism
        }
    }

    public void tampilkanByHari(String hari) {
        boolean ditemukan = false;
        for (AbstractKegiatan k : daftarKegiatan) {
            if (k.getHari().equalsIgnoreCase(hari)) {
                System.out.println("  ----");
                k.tampilkanDetail();
                ditemukan = true;
            }
        }
        if (!ditemukan) {
            System.out.println("  (Tidak ada kegiatan pada hari " + hari + ")");
        }
    }

    public int getJumlahKegiatan() {
        return daftarKegiatan.size();
    }
}
