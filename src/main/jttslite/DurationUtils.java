/*
 * Copyright (c) 2013, the original author or authors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jttslite;

import java.text.DecimalFormat;

/**
 * @author Davide Cavestro
 */
public class DurationUtils {

    private DurationUtils (){}

    private final static DurationNumberFormatter durationNumberFormatter = new DurationNumberFormatter ();

    public static class DurationNumberFormatter extends DecimalFormat {
        public DurationNumberFormatter (){
            setMinimumIntegerDigits (2);
        }
    }

    public static String format (final Duration d) {
        final StringBuilder sb = new StringBuilder ();

        sb.append (durationNumberFormatter.format (d.getTotalHours ()))
                .append (":")
                .append (durationNumberFormatter.format (d.getMinutes ()))
                .append (":")
                .append (durationNumberFormatter.format (d.getSeconds ()));
        return sb.toString ();
    }

    public static String formatDuration (final long d) {
        final StringBuilder sb = new StringBuilder ();

        final long[] f = Duration.getDurationFields (d);

        sb.append (durationNumberFormatter.format (f[Duration.HOURS_SLOT]))
                .append (":")
                .append (durationNumberFormatter.format (f[Duration.MINUTES_SLOT]))
                .append (":")
                .append (durationNumberFormatter.format (f[Duration.SECONDS_SLOT]));
        return sb.toString ();
    }



}
