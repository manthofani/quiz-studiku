# QuizApp (Studiku)

Aplikasi kuis matematika untuk Android yang dibangun menggunakan Kotlin dan Jetpack Compose. Soal kuis dibuat secara dinamis (procedural generation) berdasarkan tingkat kesulitan yang dipilih, dengan penyimpanan skor tertinggi secara lokal.

---

## Cara Menjalankan Project

### Prasyarat
- Android Studio (versi Ladybug, Hedgehog, atau lebih baru)
- JDK 17
- Android SDK Platform 35 (Target SDK 35, Minimum SDK 24)
- Perangkat Android fisik dengan USB Debugging aktif atau Android Emulator

### Langkah Menjalankan via Android Studio
1. Buka Android Studio.
2. Pilih menu **Open** dan arahkan ke direktori project `quiz-studiku`.
3. Tunggu proses sinkronisasi Gradle selesai.
4. Pastikan file `local.properties` sudah mengarah ke lokasi Android SDK Anda:
   ```properties
   sdk.dir=C\:\\Users\\<Username>\\AppData\\Local\\Android\\Sdk
   ```
5. Pilih perangkat target (Emulator atau Device) pada toolbar atas.
6. Klik tombol **Run** (atau tekan `Shift + F10`).

### Langkah Menjalankan via Terminal / Command Line
- **Build APK Debug:**
  - Windows:
    ```bash
    .\gradlew.bat assembleDebug
    ```
  - Linux / macOS:
    ```bash
    ./gradlew assembleDebug
    ```
  File APK akan berada di `app/build/outputs/apk/debug/app-debug.apk`.

- **Install langsung ke perangkat yang terhubung:**
  - Windows:
    ```bash
    .\gradlew.bat installDebug
    ```
  - Linux / macOS:
    ```bash
    ./gradlew installDebug
    ```

- **Menjalankan Unit Test:**
  - Windows:
    ```bash
    .\gradlew.bat test
    ```
  - Linux / macOS:
    ```bash
    ./gradlew test
    ```

---

## Arsitektur dan Struktur Project

Project ini mengimplementasikan pola arsitektur **MVVM (Model-View-ViewModel)** yang dipadukan dengan **Repository Pattern**.

### Alur Data
1. **View (Compose UI)** mengamati state dari ViewModel melalui `StateFlow`.
2. **ViewModel (`QuizViewModel`)** memproses aksi pengguna (memilih tingkat kesulitan, menjawab soal, navigasi kuis) dan memperbarui `QuizUiState`.
3. **Question Generator (`MathQuestionGenerator`)** memproduksi daftar soal matematika secara prosedural saat sesi kuis dimulai.
4. **Repository (`ScoreRepository`)** membaca dan menyimpan data skor tertinggi ke DataStore.

### Struktur Direktori
```
app/src/main/java/com/example/quizapp/
├── MainActivity.kt                  # Entry point activity dengan Compose NavHost
├── QuizApplication.kt             # Inisialisasi dependency level aplikasi
├── data/
│   ├── generator/
│   │   └── QuestionGenerator.kt    # Interface dan logika pembuatan soal acak
│   ├── model/
│   │   ├── Difficulty.kt           # Enum tingkat kesulitan (EASY, MEDIUM, HARD)
│   │   ├── Question.kt             # Data class model soal dan pilihan jawaban
│   │   └── QuizResult.kt           # Model hasil kuis
│   └── repository/
│       └── ScoreRepository.kt      # Pengelolaan penyimpanan skor via DataStore
├── ui/
│   ├── difficulty/                 # Layar pemilihan tingkat kesulitan
│   ├── home/                       # Layar beranda
│   ├── navigation/                 # Setup NavHost dan rute navigasi
│   ├── quiz/                       # Layar interaktif pengerjaan soal kuis
│   ├── result/                     # Layar ringkasan skor dan hasil akhir
│   └── theme/                      # Tema, warna, tipografi Material 3
└── viewmodel/
    ├── QuizUiState.kt              # Representasi state UI kuis
    └── QuizViewModel.kt            # Business logic dan state management kuis
```

---

## UI Framework

Aplikasi menggunakan **Jetpack Compose** dengan panduan desain **Material 3** (`androidx.compose.material3`).
- **Declarative UI**: Seluruh antarmuka didefinisikan menggunakan fungsi `@Composable` tanpa layout XML.
- **Edge-to-Edge Display**: Mendukung tampilan layar penuh modern dengan `enableEdgeToEdge()`.
- **Navigation Compose**: Pengalihan antar halaman (Home, Difficulty, Quiz, Result) dikelola menggunakan `NavHost` dan `NavController`.

---

## Cara Kerja Question Generator

Logika pembuatan soal diatur di dalam `MathQuestionGenerator` yang mengimplementasikan interface `QuestionGenerator`:

1. **Pembuatan Sesi Kuis (`generateQuiz`)**:
   - Menerima parameter `Difficulty` dan jumlah soal (default 10 soal).
   - Memastikan tidak ada soal yang berulang dalam satu sesi dengan memeriksa pertanyaan yang sudah dibuat menggunakan set `seenPrompts`.
2. **Kalkulasi Jawaban Benar**:
   - Angka operan dibuat secara acak sesuai batasan tingkat kesulitan.
   - Hasil jawaban dihitung dan dijadikan patokan jawaban valid.
3. **Pembuatan Pilihan Jawaban (Distractor Options)**:
   - Fungsi `createQuestion()` membuat 4 pilihan jawaban ganda yang unik.
   - Tiga pilihan salah (distractor) dibuat dari angka di sekitar jawaban benar menggunakan variasi offset (contoh: ±1, ±2, ±3, ±5, ±10) agar pilihan tampak realistis.
   - Keempat pilihan jawaban diacak urutannya (`shuffled()`) sebelum disajikan ke UI.

---

## Perbedaan Tingkat Kesulitan (Difficulty)

Tingkat kesulitan diatur oleh enum `Difficulty` yang menentukan tipe operasi matematika dan rentang angka:

| Tingkat Kesulitan | Tipe Operasi | Rentang & Ketentuan Angka |
| :--- | :--- | :--- |
| **Easy** | Penjumlahan, Pengurangan, Perkalian Dasar | - Penjumlahan: angka 2 sampai 25<br>- Pengurangan: hasil selalu positif<br>- Perkalian: satu digit (2 sampai 10) |
| **Medium** | Perkalian Menengah, Pembagian Bulat, Operasi Campuran | - Perkalian: dua digit (11–21 × 4–13)<br>- Pembagian: hasil bagi bulat tanpa desimal<br>- Operasi campuran: `(a × b) ± c` |
| **Hard** | Persentase, Pangkat Dua, Persamaan Linier, Operasi Bertingkat | - Persentase: 10%, 15%, 20%, 25%, 30%, 40%, 50%, 75% dari bilangan kelipatan (hasil bulat)<br>- Pangkat: `a² ± offset`<br>- Persamaan linier: mencari nilai `x` pada `ax ± b = c`<br>- Operasi campuran 3 tahap: `(a + b) × c - d` |

---

## Library yang Digunakan

| Library / Komponen | Keterangan |
| :--- | :--- |
| **AndroidX Core KTX** | Ekstensi Kotlin untuk API inti Android |
| **AndroidX Lifecycle (Runtime & ViewModel Compose)** | Pengelolaan siklus hidup komponen dan integrasi ViewModel ke Compose |
| **Jetpack Compose BOM & Material 3** | Komponen UI deklaratif dan sistem desain Material 3 |
| **Navigation Compose** | Navigasi antar layar berbasis deklaratif |
| **AndroidX DataStore Preferences** | Penyimpanan nilai skor terbaik (best score) secara lokal (asinkron dan reaktif) |
| **Kotlinx Coroutines (Core & Test)** | Manajemen tugas latar belakang dan penanganan aliran data asinkron (`Flow`) |
| **JUnit** | Framework untuk pengujian unit (unit test) |
