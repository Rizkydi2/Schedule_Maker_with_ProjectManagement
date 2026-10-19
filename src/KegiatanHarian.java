package src;


public class KegiatanHarian extends Kegiatan {
    
    private String kategori;
    private int prioritas;   


    public KegiatanHarian(String namaKegiatan, String hari, String waktuMulai, String waktuSelesai,
                          String kategori, int prioritas) {
        super(namaKegiatan, hari, waktuMulai, waktuSelesai);
        this.kategori = kategori;
        this.prioritas = prioritas;
    }


    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public int getPrioritas() { return prioritas; }
    public void setPrioritas(int prioritas) { this.prioritas = prioritas; }

    public String getPrioritasLabel() {
        switch (prioritas) {
            case 1: return "Tinggi";
            case 2: return "Sedang";
            case 3: return "Rendah";
            default: return "Tidak Diketahui";
        }
    }


    @Override
    public String getTipeKegiatan() {
        return "Kegiatan Harian";
    }

    @Override
    public void tampilkanDetail() {
        System.out.println("  [" + getTipeKegiatan() + "] " + getNamaKegiatan());
        System.out.println("  Hari      : " + getHari());
        System.out.println("  Waktu     : " + getWaktu());
        System.out.println("  Kategori  : " + kategori);
        System.out.println("  Prioritas : " + getPrioritasLabel());
    }
}
