package kki.seftian.aplikasiabsensi

import java.text.SimpleDateFormat
import java.util.*

class ModelAbsensi {
    var waktu : String=""
    var ngapain : String= ""
    var uid : String? = ""
    var absenApa:String=""
    var key : String = ""

    constructor()
    constructor(waktu: String, ngapain: String, uid: String?, absenApa:String) {
        this.waktu = waktu
        this.ngapain = ngapain
        this.uid = uid
        this.absenApa = absenApa
        this.key = key
    }

}