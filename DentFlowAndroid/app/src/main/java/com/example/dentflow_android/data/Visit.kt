package com.example.dentflow_android.data

data class Visit(
    val id: Int,
    val patientName: String,
    val doctorName: String,
<<<<<<< HEAD
    val serviceName: String, // np. "Przegląd", "Plombowanie" [cite: 123]
    val date: String,        // np. "2026-03-20 10:30"
    val status: String       // DONE, CONFIRMED, NO_SHOW [cite: 134]
=======
    val serviceName: String,
    val date: String,
    val status: String
>>>>>>> 0e74d92b4a2b1f6b1d9460aa7c5b9827633b416c
)

