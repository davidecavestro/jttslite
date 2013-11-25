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

import groovy.transform.ToString;

import java.util.Date;

/**
 *
 * @author Davide Cavestro
 */
@ToString(includeNames=true,includeFields=true)
public class Duration {

    public final static int MILLISECONDS = 0;
    public final static int SECONDS = 1;
    public final static int MINUTES = 2;
    public final static int HOURS = 3;
    public final static int DAYS = 4;
    public final static int MONTHS = 5;
    public final static int YEARS = 6;

    public final static long HOURS_PER_DAY = 24;
    public final static long MINUTES_PER_HOUR = 60;
    public final static long SECONDS_PER_MINUTE = 60;

    public final static long MILLISECONDS_PER_SECOND = 1000;

    public final static long MILLISECONDS_PER_MINUTE = MILLISECONDS_PER_SECOND * SECONDS_PER_MINUTE;
    public final static long MILLISECONDS_PER_HOUR = MILLISECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    public final static long MILLISECONDS_PER_DAY = MILLISECONDS_PER_HOUR * HOURS_PER_DAY;


    private Date from;
    private Date to;

    /**
     * Durata nulla.
     */
    public final static Duration ZERODURATION = new Duration (0);

    /**
     * Costruttore con data di inizio e fine periodo.
     *
     * @param from data d'inizio.
     * @param to data di fine del periodo.
     */
    public Duration(Date from, Date to) {
        this.from=from;
        this.to=to;
        computeFields ();
    }

    /**
     * Costruttore con durata in millisecondi.
     *
     * @param milliseconds il numero di millisecondi.
     */
    public Duration (final long milliseconds) {
        this.totalMilliseconds = milliseconds;
        computedTotalMilliseconds = true;
        computeFields ();
    }

    /**
     * Costruttore con durata in ore minuti secondi e millisecondi.
     *
     * @param hours ore
     * @param minutes minuti
     * @param seconds secondi
     * @param milliseconds millisecondi.
     */
    public Duration (final int hours, final int minutes, final int seconds, final int milliseconds) {
        this.totalMilliseconds = milliseconds + MILLISECONDS_PER_SECOND*seconds+MILLISECONDS_PER_MINUTE*minutes+MILLISECONDS_PER_HOUR*hours;;
        computedTotalMilliseconds = true;
        computeFields ();
    }

    private final void computeFields (){
        if (!computedTotalMilliseconds){
            computeTotalMilliseconds ();
        }
        if (!computedMilliseconds){
            computeMilliseconds ();
        }
        if (!computedSeconds){
            computeSeconds ();
        }
        if (!computedMinutes){
            computeMinutes ();
        }
        if (!computedHours){
            computeHours ();
        }
        if (!computedDays){
            computeDays ();
        }
    }

    /**
     * Indice, nell'array di interi fornito daimetodi statici di calcolo, della posizione che contiene il valore TOTALE.
     */
    private final static int TOTAL_SLOT = 0;
    /**
     * Indice, nell'array di interi fornito daimetodi statici di calcolo, della posizione che contiene il valore del RESTO.
     */
    private final static int MOD_SLOT = 1;

    private boolean computedTotalMilliseconds;

    private boolean computedMilliseconds = false;
    private long totalMilliseconds = 0;
    private long modMilliseconds = 0;

    private final void computeTotalMilliseconds (){
        this.totalMilliseconds = this.to.getTime() - this.from.getTime();
        computedTotalMilliseconds = true;
    }
    private final void computeMilliseconds (){
        this.modMilliseconds = this.totalMilliseconds % MILLISECONDS_PER_SECOND;
        computedMilliseconds = true;
    }

    private boolean computedSeconds = false;
    private long totalSeconds = 0;
    private long modSeconds = 0;
    private final void computeSeconds (){
        final long[] m = computeSeconds (totalMilliseconds);
        this.totalSeconds = m[TOTAL_SLOT];
        this.modSeconds = m[MOD_SLOT];
        computedSeconds = true;
    }

    /**
     * Ritorna un array a due elementi, contenente il numero totale di secondi corrispondenti al numero di millisecondi specificato,
     * ed il numero di minuti rimanenti, modulo {@link #SECONDS_PER_MINUTE}.
     *
     * @param millis il numero di millisecondi.
     */
    private final static long[] computeSeconds (final long millis) {
        final long total = millis / MILLISECONDS_PER_SECOND;
        final long mod = total % SECONDS_PER_MINUTE;
        return new long [] {total, mod};
    }


    private boolean computedMinutes = false;
    private long totalMinutes = 0;
    private long modMinutes = 0;
    private final void computeMinutes (){
        final long[] m = computeMinutes (totalMilliseconds);
        this.totalMinutes = m[TOTAL_SLOT];
        this.modMinutes = m[MOD_SLOT];
        computedMinutes = true;
    }

    /**
     * Ritorna un array a due elementi, contenente il numero totale di minuti corrispondenti al numerodi millisecondi specificato,
     * ed il numero di minuti rimanenti, modulo {@link #MINUTES_PER_HOUR}.
     *
     * @param millis il numero di millisecondi.
     */
    private final static long[] computeMinutes (final long millis) {
        final long total = millis / MILLISECONDS_PER_MINUTE;
        final long mod = total % MINUTES_PER_HOUR;
        return new long [] {total, mod};
    }

    private boolean computedHours = false;
    private long totalHours = 0;
    private long modHours = 0;
    private final void computeHours (){
        this.totalHours = computeTotalHours (totalMilliseconds);
        this.modHours = this.totalHours % HOURS_PER_DAY;
        computedHours = true;
    }

    private final static long computeTotalHours (final long millis){
        return  millis / MILLISECONDS_PER_HOUR;
    }

    private boolean computedDays = false;
    private long totalDays = 0;
    private final void computeDays (){
        this.totalDays = this.totalMilliseconds / MILLISECONDS_PER_DAY;
        computedDays = true;
    }

    /**
     * Ritorna i millisecondi di questa durata.
     *
     * @return i millisecondi di questa durata.
     */
    public long getMilliseconds (){
        return this.modMilliseconds;
    }

    /**
     * Ritorna i secondi di questa durata.
     *
     * @return i secondi di questa durata.
     */
    public long getSeconds (){
        return this.modSeconds;
    }

    /**
     * Ritorna i minuti di questa durata.
     *
     * @return i minuti di questa durata.
     */
    public long getMinutes (){
        return this.modMinutes;
    }

    /**
     * Ritorna le ore di questa durata.
     *
     * @return le ore di questa durata.
     */
    public long getHours (){
        return this.modHours;
    }

    /**
     * Ritorna i giorni di questa durata.
     *
     * @return i giorni di questa durata.
     */
    public long getDays (){
        return this.totalDays;
    }

    /**
     * Ritorna il numero totale di ore di questa durata.
     *
     * @return il numero totale di ore di questa durata.
     */
    public long getTotalHours (){
        return this.totalHours;
    }

    /**
     * Ritorna questa durata in millisecondi.
     *
     * @return questa durata in millisecondi.
     */
    public long getTime (){
        return this.totalMilliseconds;
    }

    /**
     * Posizione dello slot contenente il valore dei secondi, nel risultato del metodo <CODE>getDurationFields</CODE>.
     */
    public final static int SECONDS_SLOT = 0;
    /**
     * Posizione dello slot contenente il valore dei minuti, nel risultato del metodo <CODE>getDurationFields</CODE>.
     */
    public final static int MINUTES_SLOT = 1;
    /**
     * Posizione dello slot contenente il valore delle ore, nel risultato del metodo <CODE>getDurationFields</CODE>.
     */
    public final static int HOURS_SLOT = 2;
    /**
     * Ritorna un array contenente il valore, dei campi <PRE>[secondi, minuti, ore]</PRE> relatici alla durata specificata.
     * <BR>
     * Tali valori corrispondono ai valori che ritornerebbero le chiamate ai metodi <PRE>[getSeconds (), getMinutes (), getTtalHours ()] </PRE>di una Duration creta a partire dal numero di millisecondi specificato.
     *
     * @param millis il numerodi millisecondi
     * @return un array contenente il valore, dei campi <PRE>[secondi, minuti, ore]</PRE> relatici alla durata specificata.
     */
    public final static long[] getDurationFields (final long millis) {
        return new  long[] {computeSeconds (millis)[MOD_SLOT], computeMinutes (millis)[MOD_SLOT], computeTotalHours (millis)};
    }
}
