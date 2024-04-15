package model

import com.android.builder.model.ApiVersion

/**
 * Created by Ruslan Arslanov on 22/12/2019.
 */
class SdkApiVersion(val sdkVersion: Int) : ApiVersion {

    override fun getCodename(): String? = null

    override fun getApiLevel(): Int = sdkVersion

    override fun getApiString(): String = sdkVersion.toString()

}