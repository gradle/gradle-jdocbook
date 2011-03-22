/*
 * jDocBook, processing of DocBook sources
 *
 * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */



package org.jboss.gradle.plugins.jdocbook.book

import java.text.SimpleDateFormat
import org.gradle.util.ConfigureUtil
import org.jboss.jdocbook.Configuration
import org.jboss.jdocbook.Profiling
import org.jboss.jdocbook.ValueInjection

/**
 *
 * @author: Strong Liu
 */
class BookConfiguration implements Configuration {
    def Book book

    BookConfiguration(Book book) {
        this.book = book
    }
    // IMAGE URI HANDLING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    boolean useRelativeImageUris = true;

    @Override
    boolean isUseRelativeImageUris() { useRelativeImageUris }
    // AUTO-DETECT FONTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    boolean autoDetectFonts = false;

    @Override
    boolean isAutoDetectFontsEnabled() { return autoDetectFonts }
    // FONT CACHE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    boolean useFopFontCache = true;

    @Override
    boolean isUseFopFontCacheEnabled() { return useFopFontCache }
    // LOCALE SEPARATOR ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    char localeSeparator = '-';
    Map<String, String> transformerParameters = [:]
    // VALUE INJECTIONS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    def applyStandardInjectionValues = true;
    def injectionDateFormat = "yyyy-MM-dd"
    LinkedHashSet<ValueInjection> valueInjections = new LinkedHashSet<ValueInjection>();

    @Override
    LinkedHashSet<ValueInjection> getValueInjections() {
        if (applyStandardInjectionValues) {
            valueInjections.add(new ValueInjection("version", book.version));
            SimpleDateFormat dateFormat = new SimpleDateFormat(injectionDateFormat);
            valueInjections.add(new ValueInjection("today", dateFormat.format(new Date())));
        }
        return valueInjections;
    }

    public void valueInjection(String name, String value) {
        valueInjections << new ValueInjection(name, value)
    }
    /**
     * Allow configuration by closure
     *
     * @param closure The config closure
     */
    public void valueInjection(Closure closure) {
        valueInjections << ConfigureUtil.configure(closure, new ValueInjection())
    }

    /**
     * Allow configuration by map
     *
     * @param settings The user settings
     */
    public void valueInjection(Map<String, ?> settings) {
        valueInjections << ConfigureUtil.configureByMap(settings, new ValueInjection())
    }

    LinkedHashSet<String> catalogs = new LinkedHashSet<String>();

    public void catalog(String catalog) {
        catalogs.add(catalog);
    }

    @Override
    LinkedHashSet<String> getCatalogs() {
        return catalogs
    }

    def profiling = new Profiling()

    @Override
    Profiling getProfiling() {
        return profiling
    }
    /**
     * Allow config by closure
     *
     * @param closure The config closure
     */
    def profiling(Closure closure) {
        ConfigureUtil.configure(closure, profiling);
    }

    /**
     * Allow config by map
     *
     * @param settings The user settings.
     */
    def profiling(Map<String, ?> settings) {
        ConfigureUtil.configureByMap(settings, profiling);
    }

    @Override
    String getDocBookVersion() {
        return null
    }

}
