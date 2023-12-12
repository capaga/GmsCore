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

import android.os.Parcel
import android.util.Log
import com.google.android.gms.dynamic.IObjectWrapper
import com.google.android.gms.dynamic.ObjectWrapper
import com.google.android.gms.dynamic.unwrap
import com.google.android.gms.maps.model.Cap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.internal.IPolylineDelegate
import com.huawei.hms.maps.model.*
import org.microg.gms.maps.hms.utils.toGms
import org.microg.gms.maps.hms.utils.toGmsPolylineWidth
import org.microg.gms.maps.hms.utils.toHms
import org.microg.gms.maps.hms.utils.toHmsPolylineWidth

class PolylineImpl(private val polyline: Polyline, polylineOptions: PolylineOptions) :
    IPolylineDelegate.Stub() {

    private var endCustomCap: Cap? = null
    private var startCustomCap: Cap? = null

    override fun remove() {
        polyline.remove()
    }

    override fun getId(): String = polyline.id

    override fun setPoints(points: List<LatLng>) {
        polyline.points = points.map { it.toHms() }
    }

    override fun getPoints(): List<LatLng> = polyline.points.map { it.toGms() }

    override fun setWidth(width: Float) {
        polyline.width = toHmsPolylineWidth(width)
    }

    override fun getWidth(): Float {
        return toGmsPolylineWidth(polyline.width)
    }

    override fun setColor(color: Int) {
        polyline.color = color
    }

    override fun getColor(): Int {
        return polyline.color
    }

    override fun setZIndex(zIndex: Float) {
        Log.d(TAG, "setZIndex: $zIndex")
        polyline.zIndex = zIndex
    }

    override fun getZIndex(): Float {
        Log.d(TAG, "getZIndex")
        return polyline.zIndex
    }

    override fun setVisible(visible: Boolean) {
        polyline.isVisible = visible
    }

    override fun isVisible(): Boolean {
        return polyline.isVisible
    }

    override fun setGeodesic(geod: Boolean) {
        Log.d(TAG, "setGeodesic: $geod")
        polyline.isGeodesic = geod
    }

    override fun isGeodesic(): Boolean {
        Log.d(TAG, "isGeodesic")
        return polyline.isGeodesic
    }

    override fun setClickable(clickable: Boolean) {
        Log.d(TAG, "setClickable: $clickable")
        polyline.isClickable = clickable
    }

    override fun isClickable(): Boolean {
        Log.d(TAG, "isClickable")
        return polyline.isClickable
    }

    override fun equalsRemote(other: IPolylineDelegate?): Boolean = equals(other)

    override fun hashCodeRemote(): Int = hashCode()

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (other is PolylineImpl) {
            return other.id == id
        }
        return false
    }

    override fun getPattern(): List<PatternItem>? {
        Log.d(TAG, "Method: getStrokePattern")
        return polyline.pattern?.map { it.toGms() }
    }

    override fun getTag(): IObjectWrapper {
        return ObjectWrapper.wrap(polyline.tag)
    }

    override fun setJointType(jointType: Int) {
        polyline.jointType = jointType
    }

    override fun getJointType(): Int {
        return polyline.jointType
    }

    override fun setPattern(pattern: List<PatternItem>?) {
        Log.d(TAG, "Method: setStrokePattern")
        polyline.pattern = pattern?.map { it.toHms() }
    }

    override fun setTag(tag: IObjectWrapper?) {
        polyline.tag = tag.unwrap()
    }

    override fun setEndCap(endCap: Cap) {
        // FIXME
        Log.d(TAG, "Method: setEndCap")
        polyline.endCap = endCap.toHms()
    }

    override fun getEndCap(): Cap {
        // FIXME
        Log.d(TAG, "Method: getEndCap")
        return polyline.endCap.toGms()
    }

    override fun setStartCap(startCap: Cap) {
        // FIXME
        Log.d(TAG, "Method: setStartCap: $startCap")
        if (startCap.type == 3) {
            this.startCustomCap = startCap
            Log.d(TAG, "test: "+(startCap.bitmapDescriptor == null))
            Log.d(TAG, startCap.toString())
        } else {
            this.startCustomCap = null
            polyline.startCap = startCap.toHms()
        }
    }

    override fun getStartCap(): Cap {
        // FIXME
        Log.d(TAG, "Method: getStartCap")
        return polyline.startCap.toGms()
    }

    private fun Cap.toHms(): com.huawei.hms.maps.model.Cap {
        return when (type) {
            com.huawei.hms.maps.model.Cap.TYPE_BUTT_CAP -> ButtCap()
            com.huawei.hms.maps.model.Cap.TYPE_SQUARE_CAP -> SquareCap()
            com.huawei.hms.maps.model.Cap.TYPE_ROUND_CAP -> RoundCap()
            else -> ButtCap()
        }
    }

    private fun com.huawei.hms.maps.model.Cap.toGms(): Cap {
        return when (this) {
            is ButtCap -> Cap(0)
            is SquareCap -> Cap(1)
            is RoundCap -> Cap(2)
            else -> Cap(0)
        }
    }

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean =
        if (super.onTransact(code, data, reply, flags)) {
            true
        } else {
            Log.d(TAG, "onTransact [unknown]: $code, $data, $flags"); false
        }

    companion object {
        private val TAG = "GmsMapPolyline"
    }
}