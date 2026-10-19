package src;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String nama;
    private String username;
    private List<Jadwal> daftarJadwal;

    public User(String nama, String username) {
        this.nama = nama;
        this.username = username;
        this.daftarJadwal = new ArrayList<>();
    }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<Jadwal> getDaftarJadwal() { return daftarJadwal; }

    public void tambahJadwal(Jadwal jadwal) {
        daftarJadwal.add(jadwal);
        System.out.println("  ✓ Jadwal '" + jadwal.getNamaJadwal() + "' berhasil dibuat.");
    }

    public boolean hapusJadwal(int index) {
        if (index >= 0 && index < daftarJadwal.size()) {
            String nama = daftarJadwal.get(index).getNamaJadwal();
            daftarJadwal.remove(index);
            System.out.println("  ✓ Jadwal '" + nama + "' berhasil dihapus.");
            return true;
        }
        System.out.println("  ✗ Index jadwal tidak valid.");
        return false;
    }

    public Jadwal getJadwal(int index) {
        if (index >= 0 && index < daftarJadwal.size()) {
            return daftarJadwal.get(index);
        }
        return null;
    }

    public void tampilkanSemuaJadwal() {
        if (daftarJadwal.isEmpty()) {
            System.out.println("  (Belum ada jadwal yang dibuat)");
            return;
        }
        for (int i = 0; i < daftarJadwal.size(); i++) {
            Jadwal j = daftarJadwal.get(i);
            System.out.println("  " + (i + 1) + ". " + j.getNamaJadwal()
                + " [" + j.getJumlahKegiatan() + " kegiatan] - " + j.getDeskripsi());
        }
    }

    public void tampilkanProfil() {
        System.out.println("  Nama     : " + nama);
        System.out.println("  Username : " + username);
        System.out.println("  Jadwal   : " + daftarJadwal.size() + " jadwal tersimpan");
    }

    public int getJumlahJadwal() {
        return daftarJadwal.size();
    }
}
