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

import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions

open class BitmapDescriptorImpl(private val id: String, private val size: FloatArray) {
    open fun applyTo(options: MarkerOptions, anchor: FloatArray, dpiFactor: Float): MarkerOptions {
        return options.anchorMarker(anchor[0], anchor[1])
    }

    open fun applyTo(symbol: Marker, anchor: FloatArray, dpiFactor: Float) {
        symbol.setMarkerAnchor(anchor[0], anchor[1])
    }
}

class ColorBitmapDescriptorImpl(id: String, size: FloatArray, val hue: Float) : BitmapDescriptorImpl(id, size) {
    override fun applyTo(options: MarkerOptions, anchor: FloatArray, dpiFactor: Float): MarkerOptions = super.applyTo(options, anchor, dpiFactor)
        .icon(BitmapDescriptorFactory.defaultMarker(hue))
    override fun applyTo(symbol: Marker, anchor: FloatArray, dpiFactor: Float) {
        super.applyTo(symbol, anchor, dpiFactor)
        symbol.setIcon(BitmapDescriptorFactory.defaultMarker(hue))
    }
}