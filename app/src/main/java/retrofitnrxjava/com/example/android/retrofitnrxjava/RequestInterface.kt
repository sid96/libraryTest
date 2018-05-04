package retrofitnrxjava.com.example.android.retrofitnrxjava

import io.reactivex.Observable
import retrofit2.http.GET
import retrofitnrxjava.com.example.android.retrofitnrxjava.contact.Contact
//Interface used by retrofit
interface RequestInterface {

    @GET("jsonparsetutorial.txt")
    fun register(): Observable<Contact>
}