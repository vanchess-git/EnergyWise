package com.example.energywizeapp.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object EntsoRetrofitClient {
    private const val BASE_URL = "https://web-api.tp.entsoe.eu"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
        .client(HTTPLogger.getLogger())
        .build()

    val entsoEApiService: EntsoEApiService = retrofit.create(EntsoEApiService::class.java)
}

object HTTPLogger {
    fun getLogger(): OkHttpClient {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
        return client
    }
}
interface EntsoEApiService {
    @GET("/api")
    suspend fun getPrices(
        @Query("securityToken") securityToken: String,
        @Query("documentType") documentType: String,
        @Query("in_Domain") inDomain: String,
        @Query("out_Domain") outDomain: String,
        @Query("periodStart") periodStart: String,
        @Query("periodEnd") periodEnd: String
    ): PublicationMarketDocument // Return the root element directly
}

data class EntsoResponse(
    @field:Element(name = "Publication_MarketDocument")
    var publicationMarketDocument: PublicationMarketDocument
)

@Root(name = "Publication_MarketDocument")
@Namespace(reference = "urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:0")
data class PublicationMarketDocument(
    @field:Element(name = "mRID")
    var mRID: String? = null,

    @field:Element(name = "revisionNumber")
    var revisionNumber: Int = 0,

    @field:Element(name = "type")
    var type: String? = null,

    @field:Element(name = "createdDateTime")
    var createdDateTime: String? = null,

    @field:Element(name = "period.timeInterval")
    var timeInterval: TimeInterval? = null,

    @field:ElementList(inline = true, name = "TimeSeries")
    var timeSeriesList: List<TimeSeries>? = null
)

data class TimeInterval(
    @field:Element(name = "start")
    var start: String? = null,

    @field:Element(name = "end")
    var end: String? = null
)

@Root(name = "TimeSeries")
data class TimeSeries(
    @field:Element(name = "mRID")
    var mRID: Int = 0,

    @field:Element(name = "businessType")
    var businessType: String? = null,

    @field:Element(name = "in_Domain.mRID")
    var inDomainMRID: String? = null,

    @field:Element(name = "out_Domain.mRID")
    var outDomainMRID: String? = null,

    @field:Element(name = "currency_Unit.name")
    var currencyUnitName: String? = null,

    @field:Element(name = "price_Measure_Unit.name")
    var priceMeasureUnitName: String? = null,

    @field:Element(name = "curveType")
    var curveType: String? = null,

    @field:Element(name = "Period")
    var period: Period? = null
)

@Root(name = "Period")
data class Period(
    @field:Element(name = "timeInterval")
    var timeInterval: TimeInterval? = null,

    @field:Element(name = "resolution")
    var resolution: String? = null,

    @field:ElementList(inline = true, name = "Point")
    var points: List<Point>? = null
)

@Root(name = "Point")
data class Point(
    @field:Element(name = "position")
    var position: Int = 0,

    @field:Element(name = "price.amount")
    var priceAmount: Double = 0.0
)

class PriceRepository {
    private val apiService = EntsoRetrofitClient.entsoEApiService

    suspend fun getPrices(
        securityToken: String,
        documentType: String,
        inDomain: String,
        outDomain: String,
        periodStart: String,
        periodEnd: String
    ): PublicationMarketDocument {

        return apiService.getPrices(
            securityToken,
            documentType,
            inDomain,
            outDomain,
            periodStart,
            periodEnd
        )
    }
}