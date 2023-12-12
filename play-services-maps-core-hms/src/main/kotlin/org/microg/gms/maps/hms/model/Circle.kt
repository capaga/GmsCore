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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.internal.ICircleDelegate
import com.huawei.hms.maps.model.Circle
import org.microg.gms.maps.hms.utils.toGms
import org.microg.gms.maps.hms.utils.toHms

class CircleImpl(private val circle: Circle) : ICircleDelegate.Stub() {

    override fun remove() {
        circle.remove()
    }

    override fun getId(): String = circle.id

    override fun setCenter(center: LatLng) {
        circle.center = center.toHms()
    }

    override fun getCenter(): LatLng = circle.center.toGms()

    override fun setRadius(radius: Double) {
        circle.radius = radius
    }

    override fun getRadius(): Double = circle.radius

    override fun setStrokeWidth(width: Float) {
        circle.strokeWidth = width
    }

    override fun getStrokeWidth(): Float = circle.strokeWidth

    override fun setStrokeColor(color: Int) {
        circle.strokeColor = color
    }

    override fun getStrokeColor(): Int = circle.strokeColor

    override fun setTag(tag: IObjectWrapper) {
        Log.d(TAG, "Method: setTag")
        circle.setTag<Any>(tag.unwrap())
    }

    override fun getTag(): IObjectWrapper? {
        Log.d(TAG, "Method: getTag")
        return ObjectWrapper.wrap(circle.tag)
    }

    override fun setStrokePattern(pattern: List<PatternItem>?) {
        Log.d(TAG, "Method: setStrokePattern")
        circle.strokePattern = pattern?.map { it.toHms() }
    }

    override fun getStrokePattern(): List<PatternItem>? {
        Log.d(TAG, "Method: getStrokePattern")
        return circle.strokePattern?.map { it.toGms() }
    }

    override fun setFillColor(color: Int) {
        circle.fillColor = color
    }

    override fun getFillColor(): Int = circle.fillColor

    override fun setZIndex(zIndex: Float) {
        circle.zIndex = zIndex
    }

    override fun getZIndex(): Float = circle.zIndex

    override fun setVisible(visible: Boolean) {
        circle.isVisible = visible
    }

    override fun isVisible(): Boolean = circle.isVisible

    override fun setClickable(clickable: Boolean) {
        circle.isClickable = clickable
    }

    override fun isClickable(): Boolean {
        return circle.isClickable
    }

    override fun equalsRemote(other: ICircleDelegate?): Boolean = equals(other)

    override fun hashCodeRemote(): Int = hashCode()

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (other is CircleImpl) {
            return other.id == id
        }
        return false
    }

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean =
            if (super.onTransact(code, data, reply, flags)) {
                true
            } else {
                Log.d(TAG, "onTransact [unknown]: $code, $data, $flags"); false
            }

    companion object {
        val TAG = "GmsMapCircle"
    }
}