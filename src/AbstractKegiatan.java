package src;

public abstract class AbstractKegiatan {
   
    private String namaKegiatan;
    private String waktuMulai;
    private String waktuSelesai;
    private String hari;

    public AbstractKegiatan(String namaKegiatan, String hari, String waktuMulai, String waktuSelesai) {
        this.namaKegiatan = namaKegiatan;
        this.hari = hari;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
    }

    public String getNamaKegiatan() { return namaKegiatan; }
    public void setNamaKegiatan(String namaKegiatan) { this.namaKegiatan = namaKegiatan; }

    public String getWaktuMulai() { return waktuMulai; }
    public void setWaktuMulai(String waktuMulai) { this.waktuMulai = waktuMulai; }

    public String getWaktuSelesai() { return waktuSelesai; }
    public void setWaktuSelesai(String waktuSelesai) { this.waktuSelesai = waktuSelesai; }

    public String getHari() { return hari; }
    public void setHari(String hari) { this.hari = hari; }

    public abstract String getTipeKegiatan();
    public abstract void tampilkanDetail();

    public String getWaktu() {
        return waktuMulai + " - " + waktuSelesai;
    }
}
