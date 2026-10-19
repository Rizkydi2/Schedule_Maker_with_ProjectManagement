package src;


public class Kuliah extends Kegiatan {

    private String namaDosen;
    private String ruangan;
    private String kodeMataKuliah;


    public Kuliah(String namaKegiatan, String hari, String waktuMulai, String waktuSelesai,
                  String kodeMataKuliah, String namaDosen, String ruangan) {
        super(namaKegiatan, hari, waktuMulai, waktuSelesai, "Mata Kuliah");
        this.kodeMataKuliah = kodeMataKuliah;
        this.namaDosen = namaDosen;
        this.ruangan = ruangan;
    }


    public String getNamaDosen() { return namaDosen; }
    public void setNamaDosen(String namaDosen) { this.namaDosen = namaDosen; }

    public String getRuangan() { return ruangan; }
    public void setRuangan(String ruangan) { this.ruangan = ruangan; }

    public String getKodeMataKuliah() { return kodeMataKuliah; }
    public void setKodeMataKuliah(String kodeMataKuliah) { this.kodeMataKuliah = kodeMataKuliah; }


    @Override
    public String getTipeKegiatan() {
        return "Kuliah";
    }

    @Override
    public void tampilkanDetail() {
        System.out.println("  [" + getTipeKegiatan() + "] " + getNamaKegiatan() + " (" + kodeMataKuliah + ")");
        System.out.println("  Hari    : " + getHari());
        System.out.println("  Waktu   : " + getWaktu());
        System.out.println("  Dosen   : " + namaDosen);
        System.out.println("  Ruangan : " + ruangan);
    }
}
