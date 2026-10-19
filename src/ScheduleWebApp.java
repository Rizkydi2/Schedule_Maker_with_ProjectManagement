package src;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ScheduleWebApp {
    private static final int PORT = 8081;
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
    private static final List<ProjectTask> tasks = new ArrayList<>();
    private static int nextId = 10;

    public static void main(String[] args) throws Exception {
        seedData();
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", ScheduleWebApp::handleHome);
        server.createContext("/add", ScheduleWebApp::handleAdd);
        server.createContext("/delete", ScheduleWebApp::handleDelete);
        server.createContext("/reset", ScheduleWebApp::handleReset);
        server.setExecutor(null);
        server.start();
        System.out.println("Schedule Maker Web berjalan di http://localhost:" + PORT);
    }

    private static void seedData() {
        if (!tasks.isEmpty()) return;
        LocalDate d = LocalDate.of(2026, 10, 6);
        add(1, "Inisiasi proyek", "Menetapkan tujuan, ruang lingkup, dan tim proyek", List.of(), 2, d, "Project Manager", false);
        add(2, "Pengumpulan data operasional", "Observasi proses pelayanan dan pengumpulan data lapangan", List.of(1), 4, d.plusDays(2), "Data Analyst", false);
        add(3, "Wawancara stakeholder", "Mengumpulkan kebutuhan dan kendala dari pihak terkait", List.of(1), 3, d.plusDays(2), "Project Manager", false);
        add(4, "Pembersihan & validasi data", "Memeriksa kelengkapan dan konsistensi data", List.of(2), 2, d.plusDays(6), "Data Analyst", false);
        add(5, "Analisis proses & bottleneck", "Mengidentifikasi waktu tunggu, bottleneck, dan akar masalah", List.of(3,4), 4, d.plusDays(8), "Business Analyst", false);
        add(6, "Penyusunan alternatif perbaikan", "Menyusun beberapa alternatif solusi operasional", List.of(5), 3, d.plusDays(12), "Business Analyst", false);
        add(7, "Milestone: alternatif disetujui", "Keputusan stakeholder atas alternatif perbaikan", List.of(6), 1, d.plusDays(15), "Project Sponsor", true);
        add(8, "Uji coba solusi", "Melakukan pilot/implementasi terbatas dan mengukur hasil", List.of(7), 5, d.plusDays(16), "Operations Team", false);
        add(9, "Evaluasi hasil pilot", "Membandingkan hasil pilot dengan baseline", List.of(8), 3, d.plusDays(21), "Data Analyst", false);
        add(10, "Laporan & presentasi akhir", "Menyusun laporan final dan mempresentasikan rekomendasi", List.of(9), 3, d.plusDays(24), "Project Manager", true);
    }

    private static void add(int id, String name, String desc, List<Integer> deps, int duration, LocalDate start, String resource, boolean milestone) {
        LocalDate finish = start.plusDays(Math.max(1, duration) - 1L);
        tasks.add(new ProjectTask(id, name, desc, deps, duration, start, finish, resource, milestone));
    }

    private static void handleHome(HttpExchange ex) throws IOException {
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) { send(ex, 405, "Method Not Allowed"); return; }
        send(ex, 200, renderPage());
    }

    private static void handleAdd(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { send(ex, 405, "Method Not Allowed"); return; }
        Map<String,String> f = form(ex);
        String name = f.getOrDefault("name", "").trim();
        if (name.isEmpty()) { redirect(ex, "/"); return; }
        int duration = parseInt(f.get("duration"), 1);
        LocalDate start = LocalDate.parse(f.getOrDefault("start", "2026-10-06"));
        List<Integer> deps = new ArrayList<>();
        for (String s : f.getOrDefault("deps", "").split(",")) {
            if (!s.trim().isEmpty()) try { deps.add(Integer.parseInt(s.trim())); } catch (Exception ignored) {}
        }
        boolean milestone = "on".equals(f.get("milestone"));
        int id = nextId++;
        add(id, name, f.getOrDefault("description", ""), deps, duration, start, f.getOrDefault("resource", "Project Team"), milestone);
        redirect(ex, "/");
    }

    private static void handleDelete(HttpExchange ex) throws IOException {
        Map<String,String> q = formFromQuery(ex.getRequestURI().getRawQuery());
        int id = parseInt(q.get("id"), -1);
        tasks.removeIf(t -> t.getId() == id);
        for (ProjectTask t : tasks) t.getDependencies().removeIf(d -> d == id);
        redirect(ex, "/");
    }

    private static void handleReset(HttpExchange ex) throws IOException {
        tasks.clear(); nextId = 10; seedData(); redirect(ex, "/");
    }

    private static String renderPage() {
        ScheduleAnalysis.Result a = ScheduleAnalysis.analyze(tasks);
        LocalDate projectStart = tasks.stream().map(ProjectTask::getStartDate).min(LocalDate::compareTo).orElse(LocalDate.now());
        LocalDate projectFinish = tasks.stream().map(ProjectTask::getFinishDate).max(LocalDate::compareTo).orElse(projectStart);
        StringBuilder h = new StringBuilder();
        h.append("<!doctype html><html><head><meta charset='utf-8'><meta name='viewport' content='width=device-width,initial-scale=1'>");
        h.append("<title>Project Schedule Management</title><style>");
        h.append(CSS);
        h.append("</style></head><body>");
        h.append("<header><div><div class='eyebrow'>JAVA LOCALHOST • PROJECT MANAGEMENT</div><h1>Project Schedule Management</h1><p>Project Analisis dan Perbaikan Operasional Tempo Gelato</p></div><div class='badge'>localhost:8081</div></header>");
        h.append("<main>");
        h.append(cardStats(tasks.size(), projectStart, projectFinish, a));
        h.append("<section class='card'><div class='section-title'><div><span class='tag'>C.1</span><h2>Network Diagram</h2></div><p>Urutan logis dan ketergantungan antar aktivitas.</p></div><div class='network-flow'>");
        int[][] levels = {{1},{2,3},{4},{5},{6},{7},{8},{9},{10}};
        for (int li=0; li<levels.length; li++) {
            h.append("<div class='network-level'>");
            for (int id: levels[li]) {
                ProjectTask t = tasks.stream().filter(x -> x.getId()==id).findFirst().orElse(null);
                if (t==null) continue;
                h.append("<div class='node ").append(a.floatDays.getOrDefault(t.getId(),1)==0?"critical":"").append("'><div class='node-id'>A").append(t.getId()).append("</div><strong>").append(esc(t.getName())).append("</strong><small>Durasi: ").append(t.getDuration()).append(" hari</small><small>Dep: ").append(deps(t)).append("</small></div>");
            }
            h.append("</div>");
            if (li < levels.length-1) h.append("<div class='down-arrow'>↓</div>");
        }
        h.append("</div><p class='note'><b>Alur dependency:</b> A2 dan A3 berjalan setelah A1; A4 mengikuti A2; A5 menunggu A3 dan A4; setelah itu alur berlanjut sampai A10. Node merah menunjukkan aktivitas pada critical path.</p></section>");
        h.append("<section class='card'><div class='section-title'><div><span class='tag'>C.2</span><h2>Critical Path Analysis</h2></div><p>Perhitungan ES, EF, LS, LF, dan float menggunakan metode CPM.</p></div>");
        h.append("<div class='critical-banner'><b>Critical Path:</b> ").append(path(a)).append(" <span>• Durasi proyek: ").append(a.projectDuration).append(" hari</span></div>");
        h.append("<div class='table-wrap'><table><thead><tr><th>ID</th><th>Aktivitas</th><th>Durasi</th><th>ES</th><th>EF</th><th>LS</th><th>LF</th><th>Float</th><th>Status</th></tr></thead><tbody>");
        for(ProjectTask t:tasks){h.append("<tr class='").append(a.floatDays.get(t.getId())==0?"critical-row":"").append("'><td>A").append(t.getId()).append("</td><td>").append(esc(t.getName())).append("</td><td>").append(t.getDuration()).append(" hari</td><td>").append(a.earlyStart.get(t.getId())).append("</td><td>").append(a.earlyFinish.get(t.getId())).append("</td><td>").append(a.lateStart.get(t.getId())).append("</td><td>").append(a.lateFinish.get(t.getId())).append("</td><td>").append(a.floatDays.get(t.getId())).append(" hari</td><td>").append(a.floatDays.get(t.getId())==0?"<b>KRITIS</b>":"Non-kritis").append("</td></tr>");}
        h.append("</tbody></table></div><p class='note'><b>Signifikansi:</b> keterlambatan aktivitas kritis akan langsung memperpanjang durasi proyek. Aktivitas non-kritis memiliki float sehingga masih mempunyai ruang waktu tertentu tanpa mengubah tanggal selesai proyek.</p></section>");
        h.append("<section class='card'><div class='section-title'><div><span class='tag'>C.3</span><h2>Gantt Chart</h2></div><p>Rencana waktu lengkap berdasarkan tanggal mulai dan selesai.</p></div>");
        h.append(gantt(projectStart, projectFinish, a));
        h.append("</section>");
        h.append("<section class='card'><div class='section-title'><div><span class='tag'>INPUT</span><h2>Tambah Aktivitas</h2></div><p>Data aktivitas baru akan langsung masuk ke Network Diagram, CPM, dan Gantt Chart.</p></div>");
        h.append("<form method='post' action='/add' class='form-grid'><input name='name' placeholder='Nama aktivitas' required><input name='resource' placeholder='Responsible person/resource' required><input type='date' name='start' value='2026-10-06' required><input type='number' name='duration' min='1' value='1' required><input name='deps' placeholder='Dependency ID, contoh: 2,3'><input name='description' placeholder='Deskripsi'><label class='check'><input type='checkbox' name='milestone'> Milestone</label><button>+ Tambahkan Aktivitas</button></form>");
        h.append("<div class='actions'><a href='/reset'>Reset ke data proyek</a></div></section>");
        h.append("<section class='card'><div class='section-title'><div><span class='tag'>DETAIL</span><h2>Project Schedule</h2></div><p>Aktivitas, dependency, durasi, tanggal, milestone, dan resource.</p></div>");
        h.append("<div class='table-wrap'><table><thead><tr><th>ID</th><th>Aktivitas</th><th>Dependency</th><th>Durasi</th><th>Mulai</th><th>Selesai</th><th>Resource</th><th>Milestone</th><th></th></tr></thead><tbody>");
        for(ProjectTask t:tasks){h.append("<tr><td>A").append(t.getId()).append("</td><td><b>").append(esc(t.getName())).append("</b><br><small>").append(esc(t.getDescription())).append("</small></td><td>").append(deps(t)).append("</td><td>").append(t.getDuration()).append(" hari</td><td>").append(t.getStartDate()).append("</td><td>").append(t.getFinishDate()).append("</td><td>").append(esc(t.getResource())).append("</td><td>").append(t.isMilestone()?"✓":"—").append("</td><td><a class='delete' href='/delete?id=").append(t.getId()).append("'>Hapus</a></td></tr>");}
        h.append("</tbody></table></div></section>");
        h.append("</main><footer>Schedule Maker • Java Web Application • CPM • Network Diagram • Gantt Chart</footer></body></html>");
        return h.toString();
    }

    private static String cardStats(int n, LocalDate start, LocalDate finish, ScheduleAnalysis.Result a){
        return "<div class='stats'><div class='stat'><span>AKTIVITAS</span><b>"+n+"</b></div><div class='stat'><span>MULAI PROYEK</span><b>"+start.format(DATE)+"</b></div><div class='stat'><span>SELESAI PROYEK</span><b>"+finish.format(DATE)+"</b></div><div class='stat'><span>CRITICAL PATH</span><b>"+a.criticalPath.size()+" aktivitas</b></div></div>";
    }

    private static String gantt(LocalDate start, LocalDate finish, ScheduleAnalysis.Result a){
        int days=(int)(finish.toEpochDay()-start.toEpochDay()+1);
        StringBuilder s=new StringBuilder("<div class='gantt-wrap'><div class='g-head' style='--days:"+days+"'><div>Aktivitas</div>");
        for(int i=0;i<days;i++) s.append("<div>").append(start.plusDays(i).getDayOfMonth()).append("<small>").append(start.plusDays(i).getMonthValue()).append("/2026</small></div>");
        s.append("</div>");
        for(ProjectTask t:tasks){int offset=(int)(t.getStartDate().toEpochDay()-start.toEpochDay()); int width=t.getDuration(); s.append("<div class='g-row'><div class='g-name'>A").append(t.getId()).append(" • ").append(esc(t.getName())).append("</div><div class='timeline' style='--days:").append(days).append(";--offset:").append(offset).append(";--span:").append(width).append("'><div class='bar ").append(a.floatDays.get(t.getId())==0?"bar-critical":"").append("' title='").append(esc(t.getName())).append(" — ").append(width).append(" hari'></div></div></div>");}
        s.append("</div>"); return s.toString();
    }

    private static String path(ScheduleAnalysis.Result a){StringBuilder s=new StringBuilder();for(int i=0;i<a.criticalPath.size();i++){if(i>0)s.append(" → ");s.append("A").append(a.criticalPath.get(i));}return s.toString();}
    private static String deps(ProjectTask t){if(t.getDependencies().isEmpty())return "—";StringBuilder s=new StringBuilder();for(int i=0;i<t.getDependencies().size();i++){if(i>0)s.append(", ");s.append("A").append(t.getDependencies().get(i));}return s.toString();}
    private static int parseInt(String s,int d){try{return Integer.parseInt(s);}catch(Exception e){return d;}}
    private static String esc(String s){if(s==null)return "";return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");}
    private static void send(HttpExchange ex,int code,String body)throws IOException{byte[] b=body.getBytes(StandardCharsets.UTF_8);ex.getResponseHeaders().set("Content-Type","text/html; charset=UTF-8");ex.sendResponseHeaders(code,b.length);try(OutputStream os=ex.getResponseBody()){os.write(b);}}
    private static void redirect(HttpExchange ex,String location)throws IOException{ex.getResponseHeaders().set("Location",location);ex.sendResponseHeaders(303,-1);ex.close();}
    private static Map<String,String> form(HttpExchange ex)throws IOException{return formFromQuery(new String(ex.getRequestBody().readAllBytes(),StandardCharsets.UTF_8));}
    private static Map<String,String> formFromQuery(String q){Map<String,String> m=new HashMap<>();if(q==null)return m;for(String p:q.split("&")){String[] kv=p.split("=",2);if(kv.length==2)m.put(url(kv[0]),url(kv[1]));}return m;}
    private static String url(String s){try{return URLDecoder.decode(s,StandardCharsets.UTF_8);}catch(Exception e){return s;}}

    private static final String CSS="""
*{box-sizing:border-box}body{margin:0;background:#f1f5f9;color:#172033;font-family:Segoe UI,Arial,sans-serif}header{background:#0f172a;color:#fff;padding:30px 6%;display:flex;justify-content:space-between;align-items:center}h1{margin:6px 0;font-size:30px}header p{margin:0;color:#cbd5e1}.eyebrow{font-size:12px;letter-spacing:2px;color:#7dd3fc}.badge{border:1px solid #334155;padding:10px 14px;border-radius:999px;color:#bae6fd}main{max-width:1400px;margin:28px auto;padding:0 24px}.stats{display:grid;grid-template-columns:repeat(4,1fr);gap:16px;margin-bottom:20px}.stat,.card{background:#fff;border:1px solid #e2e8f0;border-radius:16px;box-shadow:0 5px 20px #0f172a0a}.stat{padding:20px}.stat span{display:block;font-size:11px;color:#64748b;letter-spacing:1px}.stat b{display:block;margin-top:8px;font-size:21px}.card{padding:25px;margin-bottom:22px}.section-title{display:flex;justify-content:space-between;align-items:end;margin-bottom:20px}.section-title h2{display:inline;margin:0 0 0 10px;font-size:21px}.section-title p{margin:0;color:#64748b}.tag{font-size:11px;background:#e0f2fe;color:#0369a1;border-radius:6px;padding:6px 8px;font-weight:700}.network-flow{display:flex;flex-direction:column;align-items:center;gap:8px;padding:10px 8px}.network-level{display:flex;justify-content:center;gap:20px;min-height:82px}.down-arrow{font-size:24px;color:#94a3b8}.network{display:flex;align-items:center;gap:10px;overflow:auto;padding:22px 8px}.node{min-width:150px;max-width:180px;padding:14px;border:2px solid #cbd5e1;border-radius:12px;background:#f8fafc}.node.critical{border-color:#ef4444;background:#fff1f2}.node-id{font-size:11px;color:#64748b;margin-bottom:5px}.node strong{font-size:13px;display:block}.node small{display:block;color:#64748b;margin-top:6px}.arrow{font-size:22px;color:#94a3b8}.note{color:#64748b;font-size:13px;line-height:1.6}.critical-banner{background:#fff1f2;border:1px solid #fecaca;color:#991b1b;border-radius:12px;padding:15px;margin-bottom:16px}.critical-banner span{float:right;font-weight:700}.table-wrap{overflow:auto}table{width:100%;border-collapse:collapse;font-size:13px}th,td{padding:12px 10px;border-bottom:1px solid #e2e8f0;text-align:left;vertical-align:top}th{background:#f8fafc;color:#475569;font-size:11px;text-transform:uppercase;letter-spacing:.5px}.critical-row{background:#fff7f7}.critical-row td:first-child{color:#dc2626;font-weight:700}.form-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:12px}.form-grid input{padding:12px;border:1px solid #cbd5e1;border-radius:9px;font:inherit}.form-grid button{padding:12px;background:#0284c7;color:#fff;border:0;border-radius:9px;font-weight:700;cursor:pointer}.check{padding:10px;color:#475569}.actions{margin-top:14px}.actions a,.delete{color:#0284c7;text-decoration:none}.delete{color:#dc2626}.gantt-wrap{overflow:auto;min-width:900px}.g-head,.g-row{display:grid;grid-template-columns:250px 1fr}.g-head>div:first-child{padding:10px;font-weight:700}.g-head>div:not(:first-child){font-size:10px;text-align:center;padding:8px 0;border-left:1px solid #e2e8f0;background:#f8fafc}.g-head>div small{display:block;color:#94a3b8}.g-row{min-height:42px;border-top:1px solid #e2e8f0}.g-name{padding:12px 10px;font-size:12px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.timeline{display:grid;grid-template-columns:repeat(var(--days),1fr);position:relative;background:repeating-linear-gradient(90deg,#fff 0,#fff calc(100% / var(--days) - 1px),#e2e8f0 calc(100% / var(--days) - 1px),#e2e8f0 calc(100% / var(--days)))}.bar{grid-column:calc(var(--offset) + 1)/span var(--span);align-self:center;height:22px;border-radius:7px;background:#38bdf8}.bar-critical{background:#ef4444}footer{text-align:center;color:#94a3b8;font-size:12px;padding:20px}@media(max-width:900px){.stats{grid-template-columns:repeat(2,1fr)}.form-grid{grid-template-columns:1fr}.section-title{display:block}.section-title p{margin-top:8px}header{display:block}.badge{display:inline-block;margin-top:15px}}
""";
}
