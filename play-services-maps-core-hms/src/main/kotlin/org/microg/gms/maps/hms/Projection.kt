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

package org.microg.gms.maps.hms

import android.graphics.Point
import android.util.Log
import com.google.android.gms.dynamic.IObjectWrapper
import com.google.android.gms.dynamic.ObjectWrapper
import com.google.android.gms.dynamic.unwrap
import com.google.android.gms.maps.internal.IProjectionDelegate
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.VisibleRegion
import com.huawei.hms.maps.Projection
import org.microg.gms.maps.hms.utils.toGms
import org.microg.gms.maps.hms.utils.toHms

// TODO: Do calculations using backed up locations instead of live (which requires UI thread)
class ProjectionImpl(private val projection: Projection) : IProjectionDelegate.Stub() {

    override fun fromScreenLocation(obj: IObjectWrapper?): LatLng? {
        Log.d(TAG, "fromScreenLocation")
        return try {
            obj.unwrap<Point>()?.let {
                projection.fromScreenLocation(it).toGms()
            }
        } catch (e: Exception) {
            Log.d(TAG, "fromScreenLocation() used from outside UI thread on map with tilt or bearing, expect bugs")
            LatLng(0.0, 0.0)
        }
    }

    override fun toScreenLocation(latLng: LatLng?): IObjectWrapper {
        Log.d(TAG, "toScreenLocation: $latLng")
        return try {
            ObjectWrapper.wrap(latLng?.toHms()?.let {
                projection.toScreenLocation(it).let { Point(it.x, it.y) }
            })
        } catch (e: Exception) {
            Log.d(TAG, "toScreenLocation() used from outside UI thread on map with tilt or bearing, expect bugs")
            ObjectWrapper.wrap(Point(0, 0))
        }
    }

    override fun getVisibleRegion(): VisibleRegion {
        val visibleRegion = projection.visibleRegion
        Log.d(TAG, "getVisibleRegion: $visibleRegion")
        return visibleRegion.toGms()
    }

    companion object {
        private val TAG = "GmsMapProjection"
    }
}
