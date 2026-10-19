package src;

public class Kegiatan extends AbstractKegiatan {

    private String keterangan;


    public Kegiatan(String namaKegiatan, String hari, String waktuMulai, String waktuSelesai, String keterangan) {
        super(namaKegiatan, hari, waktuMulai, waktuSelesai); // memanggil constructor parent
        this.keterangan = keterangan;
    }


    public Kegiatan(String namaKegiatan, String hari, String waktuMulai, String waktuSelesai) {
        super(namaKegiatan, hari, waktuMulai, waktuSelesai);
        this.keterangan = "-";
    }


    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    @Override
    public String getTipeKegiatan() {
        return "Kegiatan Umum";
    }

    @Override
    public void tampilkanDetail() {
        System.out.println("  [" + getTipeKegiatan() + "] " + getNamaKegiatan());
        System.out.println("  Hari    : " + getHari());
        System.out.println("  Waktu   : " + getWaktu());
        System.out.println("  Ket.    : " + keterangan);
    }
}
