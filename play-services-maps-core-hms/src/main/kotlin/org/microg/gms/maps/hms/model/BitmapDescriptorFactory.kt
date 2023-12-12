/*
 * Copyright (C) 2019 microG Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.microg.gms.maps.hms.model

import android.content.res.Resources
import android.graphics.*
import android.os.Parcel
import android.util.Log
import com.google.android.gms.dynamic.IObjectWrapper
import com.google.android.gms.dynamic.ObjectWrapper
import com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.model.BitmapDescriptorFactory


object BitmapDescriptorFactoryImpl : IBitmapDescriptorFactoryDelegate.Stub() {
    private val TAG = "GmsMapBitmap"
    private var resources: Resources? = null
    private var mapResources: Resources? = null
    private val maps = hashSetOf<HuaweiMap>()
    private val bitmaps = hashMapOf<String, Bitmap>()

    fun initialize(mapResources: Resources?, resources: Resources?) {
        BitmapDescriptorFactoryImpl.mapResources = mapResources ?: resources
        BitmapDescriptorFactoryImpl.resources = resources ?: mapResources
    }

    fun registerMap(map: HuaweiMap) {
        Log.d(TAG, "registerMap")
        maps.add(map)
    }

    fun unregisterMap(map: HuaweiMap?) {
        maps.remove(map)
        // TODO: cleanup bitmaps?
    }

    fun bitmapSize(id: String): FloatArray =
            bitmaps[id]?.let { floatArrayOf(it.width.toFloat(), it.height.toFloat()) }
                    ?: floatArrayOf(0f, 0f)

    private fun registerBitmap(id: String, bitmapCreator: () -> Bitmap?) {
        val bitmap: Bitmap = synchronized(bitmaps) {
            if (bitmaps.contains(id)) return
            val bitmap = bitmapCreator()
            if (bitmap == null) {
                Log.w(TAG, "Failed to register bitmap $id, creator returned null")
                return
            }
            bitmaps[id] = bitmap
            bitmap
        }
    }

    override fun fromResource(resourceId: Int): IObjectWrapper? {
        return BitmapFactory.decodeResource(resources, resourceId)?.let {
            ObjectWrapper.wrap(BitmapDescriptorFactory.fromBitmap(it))
        }
    }

    override fun fromAsset(assetName: String): IObjectWrapper? {
        return resources?.assets?.open(assetName)?.let {
            BitmapFactory.decodeStream(it)
                ?.let { ObjectWrapper.wrap(BitmapDescriptorFactory.fromBitmap(it)) }
        }
    }

    override fun fromFile(fileName: String): IObjectWrapper? {
        return BitmapFactory.decodeFile(fileName)
            ?.let { ObjectWrapper.wrap(BitmapDescriptorFactory.fromBitmap(it)) }
    }

    override fun defaultMarker(): IObjectWrapper? {
        Log.d(TAG, "defaultMarker: ")
        return ObjectWrapper.wrap(BitmapDescriptorFactory.defaultMarker())
    }

    override fun defaultMarkerWithHue(hue: Float): IObjectWrapper? {
        Log.d(TAG, "defaultMarkerWithHue: $hue")
        return ObjectWrapper.wrap(BitmapDescriptorFactory.defaultMarker(hue))
    }

    override fun fromBitmap(bitmap: Bitmap): IObjectWrapper? {
        return ObjectWrapper.wrap(BitmapDescriptorFactory.fromBitmap(bitmap))
    }

    override fun fromPath(absolutePath: String): IObjectWrapper? {
        return BitmapFactory.decodeFile(absolutePath)
            ?.let { ObjectWrapper.wrap(BitmapDescriptorFactory.fromBitmap(it)) }
    }

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean =
            if (super.onTransact(code, data, reply, flags)) {
                true
            } else {
                Log.d(TAG, "onTransact [unknown]: $code, $data, $flags"); false
            }
}
