/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data;

/**
 * This interface simply describes an object that can be barcoded to denote its location,
 * i.e. have a location String that represents a scannable barcode
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Locatable {
    /**
     * Returns the locationBarcode of this Locatable object.
     *
     * @return String locationBarcode.
     */
    public String getLocationBarcode();

    /**
     * Sets the locationBarcode of this Locatable object.
     *
     * @param locationBarcode locationBarcode.
     */
    public void setLocationBarcode(String locationBarcode);
}
