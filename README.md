# Schedule Maker – Project Schedule Management

Aplikasi web berbasis **Java** untuk memenuhi tugas Project Schedule Management. Aplikasi dijalankan melalui localhost dan menyediakan:

- Project Schedule lengkap: aktivitas, dependency, durasi, milestone, tanggal mulai/selesai, dan responsible person/resource.
- **C.1 Network Diagram** untuk menunjukkan urutan logis aktivitas.
- **C.2 Critical Path Analysis** dengan CPM: ES, EF, LS, LF, float, dan critical path.
- **C.3 Gantt Chart** untuk melihat rencana proyek secara visual.
- Form tambah aktivitas dan reset data.

## Teknologi
- Java 17+ (disarankan)
- `com.sun.net.httpserver.HttpServer` — HTTP server bawaan JDK
- HTML/CSS pada browser
- Tidak memerlukan database atau library eksternal.

## Cara menjalankan

### IntelliJ IDEA
1. Buka folder project.
2. Pastikan JDK 17 atau lebih baru.
3. Jalankan `src.Main`.
4. Buka browser: **http://localhost:8081**

### Terminal
```bash
cd Schedule_Maker-develop
mkdir -p out
javac -d out src/*.java
java -cp out src.Main
```

Kemudian buka **http://localhost:8081**.

## Data proyek contoh
Proyek contoh menggunakan konteks **Analisis dan Perbaikan Operasional Tempo Gelato** dengan aktivitas mulai dari inisiasi, pengumpulan data, wawancara, pembersihan data, analisis bottleneck, penyusunan alternatif, persetujuan, uji coba, evaluasi, hingga laporan/presentasi.
