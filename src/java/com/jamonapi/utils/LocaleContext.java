
package com.jamonapi.utils;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * 
 * Provides a context for localized parameters, mainly formatters, in thread local scope.
 * Note normally the Formatting classes are not thread safe.
 * This class uses ThreadLocal to return a unique object to each thread, so the getFloatingPointFormatter
 * returns the same object in one thread, and getIntegerFormatter returns another one in the same thread.
 * By using only LocaleContext to get Format objects instead of constructing Format objects
 * otherwise, thread-safety is ensured. This class is tuned for performance: constructing a Format
 * object is rather expensive, this class only creates them when really needed.
 * 
 */

public final class LocaleContext {

    /** the thread local storage for the locale specific formatters */
    private static final ThreadLocalFormatterStorage formatterStorage = new ThreadLocalFormatterStorage();

    /** @return the thread local storage for the fixed-point number formatting specific for the locale */
    public static DecimalFormat getFloatingPointFormatter() {
        return getFormatters().getFloatingPointFormatter();
    }

    /** @return the thread local storage for the integral number formatting specific for the locale */
    public static DecimalFormat getIntegerFormatter() {
        return getFormatters().getIntegerFormatter();
    }

    /** @return the thread local storage for the percent number formatting specific for the locale */
    public static DecimalFormat getPercentFormatter() {
        return getFormatters().getPercentFormatter();
    }

    /** @return the thread local storage for the currency formatting specific for the locale */
    public static DecimalFormat getCurrencyFormatter() {
        return getFormatters().getCurrencyFormatter();
    }

    /**
     * @return the thread local storage for the number formatting decimal grouping separator
     *         specific for the locale
     */
    public static char getDecimalGroupSeparator() {
        return getFormatters().getDecimalGroupSeparator();
    }

    /** @return the locale specific date time formatter */
    public static DateFormat getDateFormatter() {
        return getFormatters().getDateFormatter();
    }

    /**
     * Sets the locale to apply for formatting.
     * 
     * @param locale the locale to apply for formatting.
     */
    public static void setLocale(Locale locale) {
        getFormatters().setLocale(locale);
    }

    /**
     * @return Returns the thread associated, locale specific formatters
     */
    private static Formatters getFormatters() {
        return (Formatters) formatterStorage.get();
    }

    /** Inner class for storage of thread-associated formatters */
    private static final class ThreadLocalFormatterStorage extends ThreadLocal {
        /**
         * @return the current thread's initial value for this thread-local variable. This method
         *         will be invoked at most once per accessing thread for each thread-local.
         */
        @Override
        protected Object initialValue() {
            return new Formatters();
        }
    }

    /** Inner class of thread-associated formatters */
    private static class Formatters {
        private Locale locale;
        private DecimalFormat floatingPointFormatter;
        private DecimalFormat integerFormatter;

        /** the number formatting decimal grouping separator */
        private char decimalSeparator = 0;
        private DateFormat dateFormatter;
        private DecimalFormat percentFormatter;
        private DecimalFormat currencyFormatter;

        void setLocale(Locale locale) {
            this.locale = locale;
            // now all formatters need to re-created when needed, to apply the new locale
            floatingPointFormatter = null;
            integerFormatter = null;
            decimalSeparator = 0;
            dateFormatter = null;
            percentFormatter = null;
            currencyFormatter = null;
        }

        Locale getLocale() {
            if (locale == null) { // if no locale specified from client
                locale = Locale.getDefault();
            }
            return locale;
        }

        DecimalFormat getFloatingPointFormatter() {
            if (floatingPointFormatter == null) {
                floatingPointFormatter = (DecimalFormat) NumberFormat.getNumberInstance(getLocale());
                floatingPointFormatter.applyPattern("#,###.#");
            }
            return floatingPointFormatter;
        }

        DecimalFormat getIntegerFormatter() {
            if (integerFormatter == null) {
                integerFormatter = (DecimalFormat) NumberFormat.getNumberInstance(getLocale());
                integerFormatter.applyPattern("#,###");
            }
            return integerFormatter;
        }

        DecimalFormat getPercentFormatter() {
            if (percentFormatter == null) {
                percentFormatter = (DecimalFormat) NumberFormat.getPercentInstance(getLocale());
            }
            return percentFormatter;
        }

        DecimalFormat getCurrencyFormatter() {
            if (currencyFormatter == null) {
                currencyFormatter = (DecimalFormat) NumberFormat.getCurrencyInstance(getLocale());
            }
            return currencyFormatter;
        }

        char getDecimalGroupSeparator() {
            if (decimalSeparator == 0) {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(getLocale());
                decimalSeparator = (new Character(symbols.getGroupingSeparator())).charValue();
            }
            return decimalSeparator;
        }

        DateFormat getDateFormatter() {
            if (dateFormatter == null) {
                dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                        DateFormat.DEFAULT, getLocale());
            }
            return dateFormatter;
        }

    }

    /** contructs a new LocaleContext, for private use only */
    private LocaleContext() {
    }

}
