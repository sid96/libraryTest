package retrofitnrxjava.com.example.android.retrofitnrxjava.contact

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class Contact {

    @SerializedName("worldpopulation")
    @Expose
    var worldpopulation: List<Worldpopulation>? = null

}
